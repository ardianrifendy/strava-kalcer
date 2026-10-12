import Foundation

public final class FitEncoder {
    
    // Garmin FIT CRC-16 lookup table
    private static let crcTable: [UInt16] = [
        0x0000, 0xCC01, 0xD801, 0x1400, 0xF001, 0x3C00, 0x2800, 0xE401,
        0xA001, 0x6C00, 0x7800, 0xB401, 0x5000, 0x9C01, 0x8801, 0x4400
    ]
    
    public static func computeCRC(_ data: Data, initialCRC: UInt16 = 0) -> UInt16 {
        var crc = initialCRC
        for byte in data {
            var tmp = crcTable[Int(crc & 0xF ^ UInt16(byte & 0xF))]
            crc = (crc >> 4) ^ tmp
            tmp = crcTable[Int(crc & 0xF ^ UInt16(byte >> 4))]
            crc = (crc >> 4) ^ tmp
        }
        return crc
    }
    
    // FIT Base types
    private static let BASE_TYPE_UINT8: UInt8 = 0x02
    private static let BASE_TYPE_SINT16: UInt8 = 0x83
    private static let BASE_TYPE_UINT16: UInt8 = 0x84
    private static let BASE_TYPE_SINT32: UInt8 = 0x85
    private static let BASE_TYPE_UINT32: UInt8 = 0x86
    private static let BASE_TYPE_STRING: UInt8 = 0x07
    
    // FIT Global Message IDs
    private static let MESG_FILE_ID: UInt16 = 0
    private static let MESG_SPORT: UInt16 = 12
    private static let MESG_DEVICE_INFO: UInt16 = 23
    private static let MESG_EVENT: UInt16 = 21
    private static let MESG_RECORD: UInt16 = 20
    private static let MESG_LAP: UInt16 = 19
    private static let MESG_SESSION: UInt16 = 18
    private static let MESG_ACTIVITY: UInt16 = 34
    
    // FIT Epoch offset: Dec 31, 1989 00:00:00 UTC (631065600 seconds from Unix Epoch)
    private static let FIT_EPOCH_OFFSET: Int64 = 631065600
    
    public static func toFitTimestamp(epochMillis: Int64) -> UInt32 {
        let sec = epochMillis / 1000
        let fitSec = max(0, sec - FIT_EPOCH_OFFSET)
        return UInt32(fitSec)
    }

    private static func fitStringBytes(_ str: String, fixedSize: Int = 24) -> [UInt8] {
        var bytes = Array(str.utf8.prefix(fixedSize - 1))
        bytes.append(0)
        while bytes.count < fixedSize {
            bytes.append(0)
        }
        return bytes
    }

    public static func generateFitData(
        result: SimulationResult,
        device: DeviceProfile
    ) -> Data {
        var body = Data()
        let points = result.points
        if points.isEmpty { return Data() }
        
        let startFitTime = toFitTimestamp(epochMillis: points.first!.timestampEpochMillis)
        let endFitTime = toFitTimestamp(epochMillis: points.last!.timestampEpochMillis)
        let deviceNameBytes = fitStringBytes(device.modelName, fixedSize: 24)
        let isCycling = (result.settings.sport == .cycling)
        let isRunning = (result.settings.sport == .running)
        
        let sportByte: UInt8
        let sportName: String
        switch result.settings.sport {
        case .running:
            sportByte = 1
            sportName = "Running"
        case .cycling:
            sportByte = 2
            sportName = "Cycling"
        case .hiking:
            sportByte = 17
            sportName = "Hiking"
        }
        
        // -----------------------------------------------------------------
        // 1. FILE ID MESSAGE (Local 0, Global 0)
        // -----------------------------------------------------------------
        var defFileId = Data([0x40, 0x00, 0x00]) // Local 0, Def
        defFileId.append(contentsOf: withUnsafeBytes(of: MESG_FILE_ID.littleEndian) { Array($0) })
        defFileId.append(6) // 6 fields
        defFileId.append(contentsOf: [0, 1, BASE_TYPE_UINT8])
        defFileId.append(contentsOf: [1, 2, BASE_TYPE_UINT16])
        defFileId.append(contentsOf: [2, 2, BASE_TYPE_UINT16])
        defFileId.append(contentsOf: [3, 4, BASE_TYPE_UINT32])
        defFileId.append(contentsOf: [4, 4, BASE_TYPE_UINT32])
        defFileId.append(contentsOf: [8, 24, BASE_TYPE_STRING])
        body.append(defFileId)
        
        var dataFileId = Data([0x00]) // Local 0 Data
        dataFileId.append(4) // Activity
        dataFileId.append(contentsOf: withUnsafeBytes(of: device.manufacturerId.littleEndian) { Array($0) })
        dataFileId.append(contentsOf: withUnsafeBytes(of: device.productNumber.littleEndian) { Array($0) })
        dataFileId.append(contentsOf: withUnsafeBytes(of: device.serialNumber.littleEndian) { Array($0) })
        dataFileId.append(contentsOf: withUnsafeBytes(of: startFitTime.littleEndian) { Array($0) })
        dataFileId.append(contentsOf: deviceNameBytes)
        body.append(dataFileId)
        
        // -----------------------------------------------------------------
        // 2. SPORT MESSAGE (Local 7, Global 12)
        // Standar wajib Garmin & Strava untuk menetapkan jenis olahraga aktivitas
        // -----------------------------------------------------------------
        var defSport = Data([0x47, 0x00, 0x00]) // Local 7, Def
        defSport.append(contentsOf: withUnsafeBytes(of: MESG_SPORT.littleEndian) { Array($0) })
        defSport.append(3) // 3 fields
        defSport.append(contentsOf: [0, 1, BASE_TYPE_UINT8])    // sport
        defSport.append(contentsOf: [1, 1, BASE_TYPE_UINT8])    // sub_sport (0 = generic)
        defSport.append(contentsOf: [3, 16, BASE_TYPE_STRING])  // name
        body.append(defSport)
        
        var dataSport = Data([0x07]) // Local 7 Data
        dataSport.append(sportByte)
        dataSport.append(0) // sub_sport: generic
        dataSport.append(contentsOf: fitStringBytes(sportName, fixedSize: 16))
        body.append(dataSport)
        
        // -----------------------------------------------------------------
        // 3. DEVICE INFO MESSAGE (Local 1, Global 23)
        // -----------------------------------------------------------------
        var defDevInfo = Data([0x41, 0x00, 0x00]) // Local 1, Def
        defDevInfo.append(contentsOf: withUnsafeBytes(of: MESG_DEVICE_INFO.littleEndian) { Array($0) })
        defDevInfo.append(8) // 8 fields
        defDevInfo.append(contentsOf: [253, 4, BASE_TYPE_UINT32]) // timestamp
        defDevInfo.append(contentsOf: [0, 1, BASE_TYPE_UINT8])    // device_index (0 = creator)
        defDevInfo.append(contentsOf: [1, 1, BASE_TYPE_UINT8])    // device_type
        defDevInfo.append(contentsOf: [2, 2, BASE_TYPE_UINT16])   // manufacturer
        defDevInfo.append(contentsOf: [3, 4, BASE_TYPE_UINT32])   // serial_number
        defDevInfo.append(contentsOf: [4, 2, BASE_TYPE_UINT16])   // product
        defDevInfo.append(contentsOf: [5, 2, BASE_TYPE_UINT16])   // software_version
        defDevInfo.append(contentsOf: [27, 24, BASE_TYPE_STRING]) // product_name
        body.append(defDevInfo)
        
        // Device Info Record 0: Main Unit / GPS Watch
        var dataDevInfo0 = Data([0x01])
        dataDevInfo0.append(contentsOf: withUnsafeBytes(of: startFitTime.littleEndian) { Array($0) })
        dataDevInfo0.append(0) // Device Index 0 (Creator)
        dataDevInfo0.append(0) // Device Type Generic
        dataDevInfo0.append(contentsOf: withUnsafeBytes(of: device.manufacturerId.littleEndian) { Array($0) })
        dataDevInfo0.append(contentsOf: withUnsafeBytes(of: device.serialNumber.littleEndian) { Array($0) })
        dataDevInfo0.append(contentsOf: withUnsafeBytes(of: device.productNumber.littleEndian) { Array($0) })
        dataDevInfo0.append(contentsOf: withUnsafeBytes(of: device.softwareVersion.littleEndian) { Array($0) })
        dataDevInfo0.append(contentsOf: deviceNameBytes)
        body.append(dataDevInfo0)
        
        // Device Info Record 1: Paired Heart Rate Monitor (if HR data is simulated)
        if result.averageHeartRate != nil {
            let hrmNameBytes = fitStringBytes("HRM-Pro Plus", fixedSize: 24)
            var dataDevInfo1 = Data([0x01])
            dataDevInfo1.append(contentsOf: withUnsafeBytes(of: startFitTime.littleEndian) { Array($0) })
            dataDevInfo1.append(1)   // Device Index 1
            dataDevInfo1.append(120) // Device Type: Heart Rate Monitor
            dataDevInfo1.append(contentsOf: withUnsafeBytes(of: UInt16(1).littleEndian) { Array($0) }) // Garmin
            dataDevInfo1.append(contentsOf: withUnsafeBytes(of: UInt32(381928172).littleEndian) { Array($0) })
            dataDevInfo1.append(contentsOf: withUnsafeBytes(of: UInt16(3914).littleEndian) { Array($0) }) // HRM-Pro
            dataDevInfo1.append(contentsOf: withUnsafeBytes(of: UInt16(430).littleEndian) { Array($0) }) // v4.30
            dataDevInfo1.append(contentsOf: hrmNameBytes)
            body.append(dataDevInfo1)
        }
        
        // Device Info Record 2: Paired Bike Power Meter (HANYA UNTUK SEPEDA!)
        // JANGAN PERNAH menyertakan sensor sepeda pada mode Lari agar Strava tidak salah mendeteksi jenis olahraga
        if isCycling && result.averagePower != nil {
            let pwrNameBytes = fitStringBytes("Rally RS200", fixedSize: 24)
            var dataDevInfo2 = Data([0x01])
            dataDevInfo2.append(contentsOf: withUnsafeBytes(of: startFitTime.littleEndian) { Array($0) })
            dataDevInfo2.append(2)  // Device Index 2
            dataDevInfo2.append(11) // Device Type: Bike Power
            dataDevInfo2.append(contentsOf: withUnsafeBytes(of: UInt16(1).littleEndian) { Array($0) }) // Garmin
            dataDevInfo2.append(contentsOf: withUnsafeBytes(of: UInt32(389102931).littleEndian) { Array($0) })
            dataDevInfo2.append(contentsOf: withUnsafeBytes(of: UInt16(3822).littleEndian) { Array($0) }) // Rally
            dataDevInfo2.append(contentsOf: withUnsafeBytes(of: UInt16(400).littleEndian) { Array($0) }) // v4.00
            dataDevInfo2.append(contentsOf: pwrNameBytes)
            body.append(dataDevInfo2)
        }
        
        // -----------------------------------------------------------------
        // 4. EVENT MESSAGE (Local 2, Global 21)
        // -----------------------------------------------------------------
        var defEvent = Data([0x42, 0x00, 0x00]) // Local 2, Def
        defEvent.append(contentsOf: withUnsafeBytes(of: MESG_EVENT.littleEndian) { Array($0) })
        defEvent.append(3) // timestamp, event, event_type
        defEvent.append(contentsOf: [253, 4, BASE_TYPE_UINT32]) // timestamp
        defEvent.append(contentsOf: [0, 1, BASE_TYPE_UINT8])    // event: 0 = timer
        defEvent.append(contentsOf: [1, 1, BASE_TYPE_UINT8])    // event_type: 0 = start, 4 = stop_all
        body.append(defEvent)
        
        // Event Start
        var dataStartEvent = Data([0x02])
        dataStartEvent.append(contentsOf: withUnsafeBytes(of: startFitTime.littleEndian) { Array($0) })
        dataStartEvent.append(0) // timer
        dataStartEvent.append(0) // start
        body.append(dataStartEvent)
        
        // -----------------------------------------------------------------
        // 5. RECORD MESSAGE DEFINITION (Local 3, Global 20)
        // Untuk sepeda: 10 fields (menyertakan Power)
        // Untuk lari / hiking: 9 fields (murni GPS, HR, Cadence, Temp - tanpa Bike Power)
        // -----------------------------------------------------------------
        var defRecord = Data([0x43, 0x00, 0x00]) // Local 3, Def
        defRecord.append(contentsOf: withUnsafeBytes(of: MESG_RECORD.littleEndian) { Array($0) })
        
        if isCycling {
            defRecord.append(10) // 10 fields
            defRecord.append(contentsOf: [253, 4, BASE_TYPE_UINT32])
            defRecord.append(contentsOf: [0, 4, BASE_TYPE_SINT32])
            defRecord.append(contentsOf: [1, 4, BASE_TYPE_SINT32])
            defRecord.append(contentsOf: [2, 2, BASE_TYPE_UINT16])
            defRecord.append(contentsOf: [5, 4, BASE_TYPE_UINT32])
            defRecord.append(contentsOf: [6, 2, BASE_TYPE_UINT16])
            defRecord.append(contentsOf: [3, 1, BASE_TYPE_UINT8])
            defRecord.append(contentsOf: [7, 2, BASE_TYPE_UINT16]) // power (cycling)
            defRecord.append(contentsOf: [4, 1, BASE_TYPE_UINT8])
            defRecord.append(contentsOf: [13, 1, BASE_TYPE_UINT8])
        } else {
            defRecord.append(9) // 9 fields (clean running/hiking telemetry)
            defRecord.append(contentsOf: [253, 4, BASE_TYPE_UINT32])
            defRecord.append(contentsOf: [0, 4, BASE_TYPE_SINT32])
            defRecord.append(contentsOf: [1, 4, BASE_TYPE_SINT32])
            defRecord.append(contentsOf: [2, 2, BASE_TYPE_UINT16])
            defRecord.append(contentsOf: [5, 4, BASE_TYPE_UINT32])
            defRecord.append(contentsOf: [6, 2, BASE_TYPE_UINT16])
            defRecord.append(contentsOf: [3, 1, BASE_TYPE_UINT8])
            defRecord.append(contentsOf: [4, 1, BASE_TYPE_UINT8])  // cadence (RPM = SPM/2)
            defRecord.append(contentsOf: [13, 1, BASE_TYPE_UINT8])
        }
        body.append(defRecord)
        
        var wasStopped = false
        for p in points {
            // Emisi Timer Stop & Resume Event saat perhentian Auto-Pause terjadi
            // Ini mencegah algoritma Strava salah menghitung ulang moving time dari GPS
            if p.isStopped && !wasStopped {
                var dataStop = Data([0x02])
                let tStop = toFitTimestamp(epochMillis: p.timestampEpochMillis)
                dataStop.append(contentsOf: withUnsafeBytes(of: tStop.littleEndian) { Array($0) })
                dataStop.append(0) // timer
                dataStop.append(4) // stop_all
                body.append(dataStop)
                wasStopped = true
            } else if !p.isStopped && wasStopped {
                var dataResume = Data([0x02])
                let tResume = toFitTimestamp(epochMillis: p.timestampEpochMillis)
                dataResume.append(contentsOf: withUnsafeBytes(of: tResume.littleEndian) { Array($0) })
                dataResume.append(0) // timer
                dataResume.append(0) // start
                body.append(dataResume)
                wasStopped = false
            }
            
            var dataRec = Data([0x03]) // Local 3 Data
            let t = toFitTimestamp(epochMillis: p.timestampEpochMillis)
            let lat = GeoMath.toSemicircles(p.latitude)
            let lon = GeoMath.toSemicircles(p.longitude)
            let alt = UInt16(clamping: Int(((p.elevation + 500.0) * 5.0).rounded()))
            let dist = UInt32(clamping: Int((p.distanceFromStartMeters * 100.0).rounded()))
            let spd = UInt16(clamping: Int((p.speedMps * 1000.0).rounded()))
            let hr = UInt8(clamping: p.heartRate ?? 0)
            
            // Cadence untuk running disimpan sebagai RPM (SPM / 2) sesuai standar Garmin FIT Profile
            let cadRaw = p.cadence ?? 0
            let cadVal: UInt8 = isRunning ? UInt8(clamping: cadRaw / 2) : UInt8(clamping: cadRaw)
            let temp = UInt8(clamping: p.temperatureCelsius ?? 28)
            
            dataRec.append(contentsOf: withUnsafeBytes(of: t.littleEndian) { Array($0) })
            dataRec.append(contentsOf: withUnsafeBytes(of: lat.littleEndian) { Array($0) })
            dataRec.append(contentsOf: withUnsafeBytes(of: lon.littleEndian) { Array($0) })
            dataRec.append(contentsOf: withUnsafeBytes(of: alt.littleEndian) { Array($0) })
            dataRec.append(contentsOf: withUnsafeBytes(of: dist.littleEndian) { Array($0) })
            dataRec.append(contentsOf: withUnsafeBytes(of: spd.littleEndian) { Array($0) })
            dataRec.append(hr)
            if isCycling {
                let pwr = UInt16(clamping: p.powerWatts ?? 0)
                dataRec.append(contentsOf: withUnsafeBytes(of: pwr.littleEndian) { Array($0) })
            }
            dataRec.append(cadVal)
            dataRec.append(temp)
            body.append(dataRec)
        }
        
        // Final Event Stop
        var dataStopEvent = Data([0x02])
        dataStopEvent.append(contentsOf: withUnsafeBytes(of: endFitTime.littleEndian) { Array($0) })
        dataStopEvent.append(0) // timer
        dataStopEvent.append(4) // stop_all
        body.append(dataStopEvent)
        
        // -----------------------------------------------------------------
        // 6. AUTO-LAP SPLITS (Local 4, Global 19)
        // 1 KM untuk running/hiking, 5 KM untuk cycling
        // Standard Garmin 21-field schema with event tags & enhanced speed
        // -----------------------------------------------------------------
        var defLap = Data([0x44, 0x00, 0x00]) // Local 4, Def
        defLap.append(contentsOf: withUnsafeBytes(of: MESG_LAP.littleEndian) { Array($0) })
        
        if isCycling {
            defLap.append(23) // 23 fields (with cycling power)
            defLap.append(contentsOf: [253, 4, BASE_TYPE_UINT32]) // timestamp
            defLap.append(contentsOf: [0, 1, BASE_TYPE_UINT8])    // event: 9 = lap
            defLap.append(contentsOf: [1, 1, BASE_TYPE_UINT8])    // event_type: 1 = stop
            defLap.append(contentsOf: [2, 4, BASE_TYPE_UINT32])   // start_time
            defLap.append(contentsOf: [7, 4, BASE_TYPE_UINT32])   // total_elapsed_time
            defLap.append(contentsOf: [8, 4, BASE_TYPE_UINT32])   // total_timer_time
            defLap.append(contentsOf: [9, 4, BASE_TYPE_UINT32])   // total_distance
            defLap.append(contentsOf: [10, 4, BASE_TYPE_UINT32])  // total_cycles
            defLap.append(contentsOf: [11, 2, BASE_TYPE_UINT16])  // total_calories
            defLap.append(contentsOf: [13, 2, BASE_TYPE_UINT16])  // avg_speed
            defLap.append(contentsOf: [14, 2, BASE_TYPE_UINT16])  // max_speed
            defLap.append(contentsOf: [15, 1, BASE_TYPE_UINT8])   // avg_heart_rate
            defLap.append(contentsOf: [16, 1, BASE_TYPE_UINT8])   // max_heart_rate
            defLap.append(contentsOf: [17, 1, BASE_TYPE_UINT8])   // avg_cadence
            defLap.append(contentsOf: [18, 1, BASE_TYPE_UINT8])   // max_cadence
            defLap.append(contentsOf: [19, 2, BASE_TYPE_UINT16])  // avg_power
            defLap.append(contentsOf: [20, 2, BASE_TYPE_UINT16])  // max_power
            defLap.append(contentsOf: [21, 2, BASE_TYPE_UINT16])  // total_ascent
            defLap.append(contentsOf: [22, 2, BASE_TYPE_UINT16])  // total_descent
            defLap.append(contentsOf: [25, 1, BASE_TYPE_UINT8])   // sport
            defLap.append(contentsOf: [110, 4, BASE_TYPE_UINT32]) // enhanced_avg_speed
            defLap.append(contentsOf: [111, 4, BASE_TYPE_UINT32]) // enhanced_max_speed
            defLap.append(contentsOf: [254, 2, BASE_TYPE_UINT16]) // message_index
        } else {
            defLap.append(21) // 21 fields (pure running/hiking telemetry)
            defLap.append(contentsOf: [253, 4, BASE_TYPE_UINT32]) // timestamp
            defLap.append(contentsOf: [0, 1, BASE_TYPE_UINT8])    // event: 9 = lap
            defLap.append(contentsOf: [1, 1, BASE_TYPE_UINT8])    // event_type: 1 = stop
            defLap.append(contentsOf: [2, 4, BASE_TYPE_UINT32])   // start_time
            defLap.append(contentsOf: [7, 4, BASE_TYPE_UINT32])   // total_elapsed_time
            defLap.append(contentsOf: [8, 4, BASE_TYPE_UINT32])   // total_timer_time
            defLap.append(contentsOf: [9, 4, BASE_TYPE_UINT32])   // total_distance
            defLap.append(contentsOf: [10, 4, BASE_TYPE_UINT32])  // total_strides (running)
            defLap.append(contentsOf: [11, 2, BASE_TYPE_UINT16])  // total_calories
            defLap.append(contentsOf: [13, 2, BASE_TYPE_UINT16])  // avg_speed
            defLap.append(contentsOf: [14, 2, BASE_TYPE_UINT16])  // max_speed
            defLap.append(contentsOf: [15, 1, BASE_TYPE_UINT8])   // avg_heart_rate
            defLap.append(contentsOf: [16, 1, BASE_TYPE_UINT8])   // max_heart_rate
            defLap.append(contentsOf: [17, 1, BASE_TYPE_UINT8])   // avg_cadence
            defLap.append(contentsOf: [18, 1, BASE_TYPE_UINT8])   // max_cadence
            defLap.append(contentsOf: [21, 2, BASE_TYPE_UINT16])  // total_ascent
            defLap.append(contentsOf: [22, 2, BASE_TYPE_UINT16])  // total_descent
            defLap.append(contentsOf: [25, 1, BASE_TYPE_UINT8])   // sport
            defLap.append(contentsOf: [110, 4, BASE_TYPE_UINT32]) // enhanced_avg_speed
            defLap.append(contentsOf: [111, 4, BASE_TYPE_UINT32]) // enhanced_max_speed
            defLap.append(contentsOf: [254, 2, BASE_TYPE_UINT16]) // message_index
        }
        body.append(defLap)
        
        let lapInterval = isCycling ? 5000.0 : 1000.0
        var lapStartIdx = 0
        var nextLapDist = lapInterval
        var lapIndex: UInt16 = 0
        
        for i in 0..<points.count {
            let p = points[i]
            if p.distanceFromStartMeters >= nextLapDist || i == points.count - 1 {
                let startP = points[lapStartIdx]
                let lapSlice = points[lapStartIdx...i]
                let lapDist = p.distanceFromStartMeters - startP.distanceFromStartMeters
                let lapTimerTime = max(1, p.movingTimeSeconds - startP.movingTimeSeconds)
                let lapElapsedTime = max(1, p.totalTimeSeconds - startP.totalTimeSeconds)
                let avgSpd = (lapTimerTime > 0) ? (lapDist / Double(lapTimerTime)) : 0.0
                let maxSpd = lapSlice.map { $0.speedMps }.max() ?? 0.0
                
                // Lap Heart Rate
                let hrs = lapSlice.compactMap { $0.heartRate }.filter { $0 > 0 }
                let avgHr = UInt8(hrs.isEmpty ? 0 : hrs.reduce(0, +) / hrs.count)
                let maxHr = UInt8(hrs.max() ?? 0)
                
                // Lap Cadence
                let cads = lapSlice.compactMap { $0.cadence }.filter { $0 > 0 }
                let avgCadRaw = cads.isEmpty ? 0 : cads.reduce(0, +) / cads.count
                let maxCadRaw = cads.max() ?? 0
                let avgCad = isRunning ? UInt8(clamping: avgCadRaw / 2) : UInt8(clamping: avgCadRaw)
                let maxCad = isRunning ? UInt8(clamping: maxCadRaw / 2) : UInt8(clamping: maxCadRaw)
                
                // Lap Strides / Cycles
                let lapStrides = UInt32(clamping: Int((Double(avgCadRaw) * (Double(lapTimerTime) / 60.0)).rounded()))
                
                // Lap Elevation Gain & Loss
                var lapAscent = 0.0
                var lapDescent = 0.0
                if lapSlice.count > 1 {
                    var prevAlt = lapSlice.first!.elevation
                    for pt in lapSlice.dropFirst() {
                        let dE = pt.elevation - prevAlt
                        if dE > 0 { lapAscent += dE } else { lapDescent += abs(dE) }
                        prevAlt = pt.elevation
                    }
                }
                
                // Lap Calories
                let totalTimeSafe = max(1, result.movingTimeSeconds)
                let lapCal = UInt16(clamping: Int((Double(result.totalCalories ?? 0) * (Double(lapTimerTime) / Double(totalTimeSafe))).rounded()))
                
                var dataLap = Data([0x04])
                let lapEndT = toFitTimestamp(epochMillis: p.timestampEpochMillis)
                let lapStartT = toFitTimestamp(epochMillis: startP.timestampEpochMillis)
                let elapsedMs = UInt32(lapElapsedTime * 1000)
                let timerMs = UInt32(lapTimerTime * 1000)
                let distCm = UInt32(lapDist * 100.0)
                let spdMm = UInt16(clamping: Int(avgSpd * 1000.0))
                let maxSpdMm = UInt16(clamping: Int(maxSpd * 1000.0))
                let enhSpd = UInt32(clamping: Int(avgSpd * 1000.0))
                let enhMaxSpd = UInt32(clamping: Int(maxSpd * 1000.0))
                let ascentM = UInt16(clamping: Int(lapAscent.rounded()))
                let descentM = UInt16(clamping: Int(lapDescent.rounded()))
                
                dataLap.append(contentsOf: withUnsafeBytes(of: lapEndT.littleEndian) { Array($0) })
                dataLap.append(9) // event: lap
                dataLap.append(1) // event_type: stop
                dataLap.append(contentsOf: withUnsafeBytes(of: lapStartT.littleEndian) { Array($0) })
                dataLap.append(contentsOf: withUnsafeBytes(of: elapsedMs.littleEndian) { Array($0) })
                dataLap.append(contentsOf: withUnsafeBytes(of: timerMs.littleEndian) { Array($0) })
                dataLap.append(contentsOf: withUnsafeBytes(of: distCm.littleEndian) { Array($0) })
                dataLap.append(contentsOf: withUnsafeBytes(of: lapStrides.littleEndian) { Array($0) })
                dataLap.append(contentsOf: withUnsafeBytes(of: lapCal.littleEndian) { Array($0) })
                dataLap.append(contentsOf: withUnsafeBytes(of: spdMm.littleEndian) { Array($0) })
                dataLap.append(contentsOf: withUnsafeBytes(of: maxSpdMm.littleEndian) { Array($0) })
                dataLap.append(avgHr)
                dataLap.append(maxHr)
                dataLap.append(avgCad)
                dataLap.append(maxCad)
                if isCycling {
                    let pwrs = lapSlice.compactMap { $0.powerWatts }.filter { $0 > 0 }
                    let avgPwr = UInt16(clamping: pwrs.isEmpty ? 0 : pwrs.reduce(0, +) / pwrs.count)
                    let maxPwr = UInt16(clamping: lapSlice.compactMap { $0.powerWatts }.max() ?? 0)
                    dataLap.append(contentsOf: withUnsafeBytes(of: avgPwr.littleEndian) { Array($0) })
                    dataLap.append(contentsOf: withUnsafeBytes(of: maxPwr.littleEndian) { Array($0) })
                }
                dataLap.append(contentsOf: withUnsafeBytes(of: ascentM.littleEndian) { Array($0) })
                dataLap.append(contentsOf: withUnsafeBytes(of: descentM.littleEndian) { Array($0) })
                dataLap.append(sportByte)
                dataLap.append(contentsOf: withUnsafeBytes(of: enhSpd.littleEndian) { Array($0) })
                dataLap.append(contentsOf: withUnsafeBytes(of: enhMaxSpd.littleEndian) { Array($0) })
                dataLap.append(contentsOf: withUnsafeBytes(of: lapIndex.littleEndian) { Array($0) })
                body.append(dataLap)
                
                lapIndex += 1
                lapStartIdx = i
                nextLapDist += lapInterval
            }
        }
        
        // -----------------------------------------------------------------
        // 7. SESSION MESSAGE (Local 5, Global 18)
        // Complete aggregate statistics for Strava Activity header
        // Memuat event: session stop, total_moving_time, enhanced_avg_speed, total_strides
        // -----------------------------------------------------------------
        var defSession = Data([0x45, 0x00, 0x00]) // Local 5, Def
        defSession.append(contentsOf: withUnsafeBytes(of: MESG_SESSION.littleEndian) { Array($0) })
        
        if isCycling {
            defSession.append(24) // 24 fields (with cycling power)
            defSession.append(contentsOf: [253, 4, BASE_TYPE_UINT32]) // timestamp
            defSession.append(contentsOf: [0, 1, BASE_TYPE_UINT8])    // event: 8 = session
            defSession.append(contentsOf: [1, 1, BASE_TYPE_UINT8])    // event_type: 1 = stop
            defSession.append(contentsOf: [2, 4, BASE_TYPE_UINT32])   // start_time
            defSession.append(contentsOf: [5, 1, BASE_TYPE_UINT8])    // sport
            defSession.append(contentsOf: [6, 1, BASE_TYPE_UINT8])    // sub_sport (0 = generic)
            defSession.append(contentsOf: [7, 4, BASE_TYPE_UINT32])   // total_elapsed_time
            defSession.append(contentsOf: [8, 4, BASE_TYPE_UINT32])   // total_timer_time
            defSession.append(contentsOf: [9, 4, BASE_TYPE_UINT32])   // total_distance
            defSession.append(contentsOf: [10, 4, BASE_TYPE_UINT32])  // total_cycles
            defSession.append(contentsOf: [11, 2, BASE_TYPE_UINT16])  // total_calories
            defSession.append(contentsOf: [14, 2, BASE_TYPE_UINT16])  // avg_speed
            defSession.append(contentsOf: [15, 2, BASE_TYPE_UINT16])  // max_speed
            defSession.append(contentsOf: [16, 1, BASE_TYPE_UINT8])   // avg_heart_rate
            defSession.append(contentsOf: [17, 1, BASE_TYPE_UINT8])   // max_heart_rate
            defSession.append(contentsOf: [18, 1, BASE_TYPE_UINT8])   // avg_cadence
            defSession.append(contentsOf: [19, 1, BASE_TYPE_UINT8])   // max_cadence
            defSession.append(contentsOf: [20, 2, BASE_TYPE_UINT16])  // avg_power
            defSession.append(contentsOf: [21, 2, BASE_TYPE_UINT16])  // max_power
            defSession.append(contentsOf: [22, 2, BASE_TYPE_UINT16])  // total_ascent
            defSession.append(contentsOf: [23, 2, BASE_TYPE_UINT16])  // total_descent
            defSession.append(contentsOf: [59, 4, BASE_TYPE_UINT32])  // total_moving_time (krusial untuk Strava!)
            defSession.append(contentsOf: [124, 4, BASE_TYPE_UINT32]) // enhanced_avg_speed
            defSession.append(contentsOf: [125, 4, BASE_TYPE_UINT32]) // enhanced_max_speed
        } else {
            defSession.append(22) // 22 fields (pure running/hiking telemetry)
            defSession.append(contentsOf: [253, 4, BASE_TYPE_UINT32]) // timestamp
            defSession.append(contentsOf: [0, 1, BASE_TYPE_UINT8])    // event: 8 = session
            defSession.append(contentsOf: [1, 1, BASE_TYPE_UINT8])    // event_type: 1 = stop
            defSession.append(contentsOf: [2, 4, BASE_TYPE_UINT32])   // start_time
            defSession.append(contentsOf: [5, 1, BASE_TYPE_UINT8])    // sport
            defSession.append(contentsOf: [6, 1, BASE_TYPE_UINT8])    // sub_sport (0 = generic)
            defSession.append(contentsOf: [7, 4, BASE_TYPE_UINT32])   // total_elapsed_time
            defSession.append(contentsOf: [8, 4, BASE_TYPE_UINT32])   // total_timer_time
            defSession.append(contentsOf: [9, 4, BASE_TYPE_UINT32])   // total_distance
            defSession.append(contentsOf: [10, 4, BASE_TYPE_UINT32])  // total_strides (running)
            defSession.append(contentsOf: [11, 2, BASE_TYPE_UINT16])  // total_calories
            defSession.append(contentsOf: [14, 2, BASE_TYPE_UINT16])  // avg_speed
            defSession.append(contentsOf: [15, 2, BASE_TYPE_UINT16])  // max_speed
            defSession.append(contentsOf: [16, 1, BASE_TYPE_UINT8])   // avg_heart_rate
            defSession.append(contentsOf: [17, 1, BASE_TYPE_UINT8])   // max_heart_rate
            defSession.append(contentsOf: [18, 1, BASE_TYPE_UINT8])   // avg_cadence
            defSession.append(contentsOf: [19, 1, BASE_TYPE_UINT8])   // max_cadence
            defSession.append(contentsOf: [22, 2, BASE_TYPE_UINT16])  // total_ascent
            defSession.append(contentsOf: [23, 2, BASE_TYPE_UINT16])  // total_descent
            defSession.append(contentsOf: [59, 4, BASE_TYPE_UINT32])  // total_moving_time (krusial untuk Strava!)
            defSession.append(contentsOf: [124, 4, BASE_TYPE_UINT32]) // enhanced_avg_speed
            defSession.append(contentsOf: [125, 4, BASE_TYPE_UINT32]) // enhanced_max_speed
        }
        body.append(defSession)
        
        var dataSession = Data([0x05])
        let totalElapsedMs = UInt32(result.totalTimeSeconds * 1000)
        let totalTimerMs = UInt32(result.movingTimeSeconds * 1000)
        let totalDistCm = UInt32(result.totalDistanceMeters * 100.0)
        let avgSpdMm = UInt16(clamping: Int(GeoMath.kmhToMps(result.averageMovingSpeedKmh) * 1000.0))
        let maxSpdMm = UInt16(clamping: Int(GeoMath.kmhToMps(result.maxSpeedKmh) * 1000.0))
        let enhAvgSpd = UInt32(clamping: Int(GeoMath.kmhToMps(result.averageMovingSpeedKmh) * 1000.0))
        let enhMaxSpd = UInt32(clamping: Int(GeoMath.kmhToMps(result.maxSpeedKmh) * 1000.0))
        let cal = UInt16(clamping: result.totalCalories ?? 0)
        
        let avgHr = UInt8(clamping: result.averageHeartRate ?? 0)
        let maxHr = UInt8(clamping: result.maxHeartRate ?? 0)
        let avgCadRaw = result.averageCadence ?? 0
        let maxCadRaw = result.maxCadence ?? 0
        let avgCad = isRunning ? UInt8(clamping: avgCadRaw / 2) : UInt8(clamping: avgCadRaw)
        let maxCad = isRunning ? UInt8(clamping: maxCadRaw / 2) : UInt8(clamping: maxCadRaw)
        
        // Total steps/strides atau total pedal cycles
        let totalStrides: UInt32 = {
            let spmOrRpm = Double(avgCadRaw > 0 ? avgCadRaw : (isRunning ? 165 : 85))
            let mins = Double(result.movingTimeSeconds) / 60.0
            return UInt32(clamping: Int((spmOrRpm * mins).rounded()))
        }()
        
        let totalAscent = UInt16(clamping: Int(result.elevationGainMeters.rounded()))
        let totalDescent = UInt16(clamping: Int(result.elevationLossMeters.rounded()))
        
        dataSession.append(contentsOf: withUnsafeBytes(of: endFitTime.littleEndian) { Array($0) })
        dataSession.append(8) // event: session
        dataSession.append(1) // event_type: stop
        dataSession.append(contentsOf: withUnsafeBytes(of: startFitTime.littleEndian) { Array($0) })
        dataSession.append(sportByte)
        dataSession.append(0) // sub_sport generic
        dataSession.append(contentsOf: withUnsafeBytes(of: totalElapsedMs.littleEndian) { Array($0) })
        dataSession.append(contentsOf: withUnsafeBytes(of: totalTimerMs.littleEndian) { Array($0) })
        dataSession.append(contentsOf: withUnsafeBytes(of: totalDistCm.littleEndian) { Array($0) })
        dataSession.append(contentsOf: withUnsafeBytes(of: totalStrides.littleEndian) { Array($0) })
        dataSession.append(contentsOf: withUnsafeBytes(of: cal.littleEndian) { Array($0) })
        dataSession.append(contentsOf: withUnsafeBytes(of: avgSpdMm.littleEndian) { Array($0) })
        dataSession.append(contentsOf: withUnsafeBytes(of: maxSpdMm.littleEndian) { Array($0) })
        dataSession.append(avgHr)
        dataSession.append(maxHr)
        dataSession.append(avgCad)
        dataSession.append(maxCad)
        if isCycling {
            let avgPwr = UInt16(clamping: result.averagePower ?? 0)
            let maxPwr = UInt16(clamping: result.maxPower ?? 0)
            dataSession.append(contentsOf: withUnsafeBytes(of: avgPwr.littleEndian) { Array($0) })
            dataSession.append(contentsOf: withUnsafeBytes(of: maxPwr.littleEndian) { Array($0) })
        }
        dataSession.append(contentsOf: withUnsafeBytes(of: totalAscent.littleEndian) { Array($0) })
        dataSession.append(contentsOf: withUnsafeBytes(of: totalDescent.littleEndian) { Array($0) })
        dataSession.append(contentsOf: withUnsafeBytes(of: totalTimerMs.littleEndian) { Array($0) }) // total_moving_time
        dataSession.append(contentsOf: withUnsafeBytes(of: enhAvgSpd.littleEndian) { Array($0) })
        dataSession.append(contentsOf: withUnsafeBytes(of: enhMaxSpd.littleEndian) { Array($0) })
        body.append(dataSession)
        
        // -----------------------------------------------------------------
        // 8. ACTIVITY MESSAGE (Local 6, Global 34)
        // -----------------------------------------------------------------
        var defActivity = Data([0x46, 0x00, 0x00]) // Local 6, Def
        defActivity.append(contentsOf: withUnsafeBytes(of: MESG_ACTIVITY.littleEndian) { Array($0) })
        defActivity.append(3)
        defActivity.append(contentsOf: [253, 4, BASE_TYPE_UINT32]) // timestamp
        defActivity.append(contentsOf: [0, 4, BASE_TYPE_UINT32])   // total_timer_time
        defActivity.append(contentsOf: [1, 2, BASE_TYPE_UINT16])   // num_sessions
        body.append(defActivity)
        
        var dataActivity = Data([0x06])
        dataActivity.append(contentsOf: withUnsafeBytes(of: endFitTime.littleEndian) { Array($0) })
        dataActivity.append(contentsOf: withUnsafeBytes(of: totalTimerMs.littleEndian) { Array($0) })
        dataActivity.append(contentsOf: withUnsafeBytes(of: UInt16(1).littleEndian) { Array($0) })
        body.append(dataActivity)
        
        // -----------------------------------------------------------------
        // 9. COMPOSE 14-BYTE HEADER & COMPUTE CRCs
        // -----------------------------------------------------------------
        var header = Data()
        let headerSize: UInt8 = 14
        let protocolVersion: UInt8 = 0x20 // FIT Protocol 2.0
        let profileVersion: UInt16 = 2176  // FIT Profile 21.76
        let dataSize = UInt32(body.count)
        let fitTag: [UInt8] = [0x2E, 0x46, 0x49, 0x54] // ".FIT"
        
        header.append(headerSize)
        header.append(protocolVersion)
        header.append(contentsOf: withUnsafeBytes(of: profileVersion.littleEndian) { Array($0) })
        header.append(contentsOf: withUnsafeBytes(of: dataSize.littleEndian) { Array($0) })
        header.append(contentsOf: fitTag)
        
        // Compute Header CRC
        let headerCRC = computeCRC(header)
        header.append(contentsOf: withUnsafeBytes(of: headerCRC.littleEndian) { Array($0) })
        
        var fullFile = Data()
        fullFile.append(header)
        fullFile.append(body)
        
        // Compute Data CRC
        let dataCRC = computeCRC(body)
        fullFile.append(contentsOf: withUnsafeBytes(of: dataCRC.littleEndian) { Array($0) })
        
        return fullFile
    }
}
