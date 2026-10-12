import Foundation

public struct StravaTokenResponse: Decodable {
    public let accessToken: String
    public let refreshToken: String
    public let expiresAt: Int64
    public let athleteName: String
    
    enum CodingKeys: String, CodingKey {
        case accessToken = "access_token"
        case refreshToken = "refresh_token"
        case expiresAt = "expires_at"
        case athlete
    }
    
    public init(from decoder: Decoder) throws {
        let container = try decoder.container(keyedBy: CodingKeys.self)
        self.accessToken = try container.decode(String.self, forKey: .accessToken)
        self.refreshToken = try container.decode(String.self, forKey: .refreshToken)
        self.expiresAt = try container.decode(Int64.self, forKey: .expiresAt)
        
        if let athleteDict = try? container.nestedContainer(keyedBy: DynamicCodingKeys.self, forKey: .athlete) {
            let first = (try? athleteDict.decode(String.self, forKey: DynamicCodingKeys(stringValue: "firstname")!)) ?? ""
            let last = (try? athleteDict.decode(String.self, forKey: DynamicCodingKeys(stringValue: "lastname")!)) ?? ""
            self.athleteName = "\(first) \(last)".trimmingCharacters(in: .whitespaces)
        } else {
            self.athleteName = "Strava Athlete"
        }
    }
}

private struct DynamicCodingKeys: CodingKey {
    var stringValue: String
    init?(stringValue: String) { self.stringValue = stringValue }
    var intValue: Int? { nil }
    init?(intValue: Int) { nil }
}

public struct StravaUploadResult: Codable {
    public let uploadId: Int64
    public let status: String
    public let isReady: Bool
    public let activityId: Int64?
    public let error: String?
}

public final class StravaApiClient {
    
    private static let apiBase = "https://www.strava.com/api/v3"
    private static let oauthTokenUrl = "https://www.strava.com/oauth/token"
    
    public static func buildAuthUrl(clientId: String, redirectUri: String) -> URL? {
        var components = URLComponents(string: "https://www.strava.com/oauth/mobile/authorize")
        components?.queryItems = [
            URLQueryItem(name: "client_id", value: clientId),
            URLQueryItem(name: "redirect_uri", value: redirectUri),
            URLQueryItem(name: "response_type", value: "code"),
            URLQueryItem(name: "approval_prompt", value: "auto"),
            URLQueryItem(name: "scope", value: "activity:write,read")
        ]
        return components?.url
    }
    
    public static func exchangeCodeForToken(
        clientId: String,
        clientSecret: String,
        code: String
    ) async throws -> StravaTokenResponse {
        var req = URLRequest(url: URL(string: oauthTokenUrl)!)
        req.httpMethod = "POST"
        req.setValue("application/x-www-form-urlencoded", forHTTPHeaderField: "Content-Type")
        
        let params = "client_id=\(clientId)&client_secret=\(clientSecret)&code=\(code)&grant_type=authorization_code"
        req.httpBody = params.data(using: .utf8)
        
        let (data, response) = try await URLSession.shared.data(for: req)
        guard let httpResponse = response as? HTTPURLResponse, (200...299).contains(httpResponse.statusCode) else {
            let errString = String(data: data, encoding: .utf8) ?? "Unknown server error"
            throw NSError(domain: "StravaApi", code: -1, userInfo: [NSLocalizedDescriptionKey: errString])
        }
        
        return try JSONDecoder().decode(StravaTokenResponse.self, from: data)
    }
    
    public static func uploadFitActivity(
        accessToken: String,
        fitData: Data,
        activityName: String,
        description: String,
        sportType: SportType
    ) async throws -> Int64 {
        let boundary = "Boundary-\(UUID().uuidString)"
        var req = URLRequest(url: URL(string: "\(apiBase)/uploads")!)
        req.httpMethod = "POST"
        req.setValue("Bearer \(accessToken)", forHTTPHeaderField: "Authorization")
        req.setValue("multipart/form-data; boundary=\(boundary)", forHTTPHeaderField: "Content-Type")
        
        var body = Data()
        let stravaSport = (sportType == .cycling) ? "Ride" : "Run"
        
        func appendFormField(name: String, value: String) {
            body.append("--\(boundary)\r\n".data(using: .utf8)!)
            body.append("Content-Disposition: form-data; name=\"\(name)\"\r\n\r\n".data(using: .utf8)!)
            body.append("\(value)\r\n".data(using: .utf8)!)
        }
        
        appendFormField(name: "data_type", value: "fit")
        appendFormField(name: "name", value: activityName)
        appendFormField(name: "description", value: description)
        appendFormField(name: "sport_type", value: stravaSport)
        appendFormField(name: "trainer", value: "0")
        appendFormField(name: "commute", value: "0")
        
        // File part
        body.append("--\(boundary)\r\n".data(using: .utf8)!)
        body.append("Content-Disposition: form-data; name=\"file\"; filename=\"activity.fit\"\r\n".data(using: .utf8)!)
        body.append("Content-Type: application/octet-stream\r\n\r\n".data(using: .utf8)!)
        body.append(fitData)
        body.append("\r\n".data(using: .utf8)!)
        body.append("--\(boundary)--\r\n".data(using: .utf8)!)
        
        req.httpBody = body
        
        let (data, response) = try await URLSession.shared.data(for: req)
        guard let httpResponse = response as? HTTPURLResponse, (200...299).contains(httpResponse.statusCode) else {
            let errString = String(data: data, encoding: .utf8) ?? "Upload request failed"
            throw NSError(domain: "StravaApi", code: -2, userInfo: [NSLocalizedDescriptionKey: errString])
        }
        
        guard let json = try JSONSerialization.jsonObject(with: data) as? [String: Any],
              let uploadId = json["id"] as? Int64 ?? (json["id"] as? Int).map({ Int64($0) }) else {
            throw NSError(domain: "StravaApi", code: -3, userInfo: [NSLocalizedDescriptionKey: "Malformed upload ID response"])
        }
        
        return uploadId
    }
    
    public static func pollUploadStatus(
        accessToken: String,
        uploadId: Int64,
        maxAttempts: Int = 10
    ) async -> StravaUploadResult {
        var attempts = 0
        while attempts < maxAttempts {
            attempts += 1
            do {
                var req = URLRequest(url: URL(string: "\(apiBase)/uploads/\(uploadId)")!)
                req.setValue("Bearer \(accessToken)", forHTTPHeaderField: "Authorization")
                
                let (data, response) = try await URLSession.shared.data(for: req)
                if let http = response as? HTTPURLResponse, http.statusCode == 200,
                   let json = try JSONSerialization.jsonObject(with: data) as? [String: Any] {
                    let status = json["status"] as? String ?? ""
                    let err = json["error"] as? String
                    let actId = (json["activity_id"] as? Int64) ?? (json["activity_id"] as? Int).map { Int64($0) }
                    
                    if let err = err, !err.isEmpty {
                        return StravaUploadResult(uploadId: uploadId, status: status, isReady: false, activityId: actId, error: err)
                    }
                    if let actId = actId, actId > 0 {
                        return StravaUploadResult(uploadId: uploadId, status: status, isReady: true, activityId: actId, error: nil)
                    }
                }
            } catch {
                // Retry on transient network blip
            }
            try? await Task.sleep(nanoseconds: 1_500_000_000)
        }
        return StravaUploadResult(uploadId: uploadId, status: "Uploaded. Processing on Strava...", isReady: true, activityId: nil, error: nil)
    }
}
