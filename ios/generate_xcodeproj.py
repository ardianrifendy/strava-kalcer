import os
import uuid

def gen_id():
    return uuid.uuid4().hex[:24].upper()

project_dir = os.path.dirname(os.path.abspath(__file__))
xcodeproj_dir = os.path.join(project_dir, "StravaKalcer.xcodeproj")
os.makedirs(xcodeproj_dir, exist_ok=True)

# List of swift files to include
core_files = [
    "Models.swift",
    "GeoMath.swift",
    "GpxParser.swift",
    "Physiology.swift",
    "SimulationEngine.swift",
    "DeviceProfile.swift",
    "FitEncoder.swift",
    "StravaApiClient.swift",
    "RouteCreatorEngine.swift"
]

app_files = [
    "AppViewModel.swift",
    "Views.swift",
    "FeedAndHistoryViews.swift",
    "RouteStudioView.swift",
    "StravaKalcerApp.swift"
]

# File IDs and Build File IDs
file_refs = {}
build_files = {}

for f in core_files:
    file_refs[f] = gen_id()
    build_files[f] = gen_id()

for f in app_files:
    file_refs[f] = gen_id()
    build_files[f] = gen_id()

info_plist_id = gen_id()
sample_gpx_id = gen_id()
sample_gpx_build_id = gen_id()
assets_id = gen_id()
assets_build_id = gen_id()

root_group_id = gen_id()
core_group_id = gen_id()
app_group_id = gen_id()
products_group_id = gen_id()
product_app_id = gen_id()

sources_phase_id = gen_id()
resources_phase_id = gen_id()
frameworks_phase_id = gen_id()
target_id = gen_id()
project_id = gen_id()

target_debug_cfg_id = gen_id()
target_release_cfg_id = gen_id()
target_cfg_list_id = gen_id()

proj_debug_cfg_id = gen_id()
proj_release_cfg_id = gen_id()
proj_cfg_list_id = gen_id()

pbx = f"""// !$*UTF8*$!
{{
	archiveVersion = 1;
	classes = {{
	}};
	objectVersion = 56;
	objects = {{

/* Begin PBXBuildFile section */
"""

for f in core_files + app_files:
    pbx += f"\t\t{build_files[f]} /* {f} in Sources */ = {{isa = PBXBuildFile; fileRef = {file_refs[f]} /* {f} */; }};\n"
pbx += f"\t\t{sample_gpx_build_id} /* sample_loop.gpx in Resources */ = {{isa = PBXBuildFile; fileRef = {sample_gpx_id} /* sample_loop.gpx */; }};\n"
pbx += f"\t\t{assets_build_id} /* Assets.xcassets in Resources */ = {{isa = PBXBuildFile; fileRef = {assets_id} /* Assets.xcassets */; }};\n"

pbx += """/* End PBXBuildFile section */

/* Begin PBXFileReference section */
"""

for f in core_files:
    pbx += f'\t\t{file_refs[f]} /* {f} */ = {{isa = PBXFileReference; lastKnownFileType = sourcecode.swift; path = "{f}"; sourceTree = "<group>"; }};\n'

for f in app_files:
    pbx += f'\t\t{file_refs[f]} /* {f} */ = {{isa = PBXFileReference; lastKnownFileType = sourcecode.swift; path = "{f}"; sourceTree = "<group>"; }};\n'

pbx += f'\t\t{info_plist_id} /* Info.plist */ = {{isa = PBXFileReference; lastKnownFileType = text.plist.xml; path = "Info.plist"; sourceTree = "<group>"; }};\n'
pbx += f'\t\t{assets_id} /* Assets.xcassets */ = {{isa = PBXFileReference; lastKnownFileType = folder.assetcatalog; path = "Assets.xcassets"; sourceTree = "<group>"; }};\n'
pbx += f'\t\t{sample_gpx_id} /* sample_loop.gpx */ = {{isa = PBXFileReference; lastKnownFileType = text.xml; path = "sample_loop.gpx"; sourceTree = "<group>"; }};\n'
pbx += f'\t\t{product_app_id} /* StravaKalcer.app */ = {{isa = PBXFileReference; explicitFileType = wrapper.application; includeInIndex = 0; path = "StravaKalcer.app"; sourceTree = BUILT_PRODUCTS_DIR; }};\n'

pbx += """/* End PBXFileReference section */

/* Begin PBXFrameworksBuildPhase section */
"""
pbx += f"""\t\t{frameworks_phase_id} /* Frameworks */ = {{
			isa = PBXFrameworksBuildPhase;
			buildActionMask = 2147483647;
			files = (
			);
			runOnlyForDeploymentPostprocessing = 0;
		}};
/* End PBXFrameworksBuildPhase section */

/* Begin PBXGroup section */
"""

core_children = "\n".join([f"\t\t\t\t{file_refs[f]} /* {f} */," for f in core_files])
app_children = "\n".join([f"\t\t\t\t{file_refs[f]} /* {f} */," for f in app_files]) + f"\n\t\t\t\t{info_plist_id} /* Info.plist */," + f"\n\t\t\t\t{assets_id} /* Assets.xcassets */,"

pbx += f"""\t\t{root_group_id} = {{
			isa = PBXGroup;
			children = (
				{core_group_id} /* StravaKalcerCore */,
				{app_group_id} /* StravaKalcerApp */,
				{sample_gpx_id} /* sample_loop.gpx */,
				{products_group_id} /* Products */,
			);
			sourceTree = "<group>";
		}};
		{core_group_id} /* StravaKalcerCore */ = {{
			isa = PBXGroup;
			children = (
{core_children}
			);
			path = Sources/StravaKalcerCore;
			sourceTree = "<group>";
		}};
		{app_group_id} /* StravaKalcerApp */ = {{
			isa = PBXGroup;
			children = (
{app_children}
			);
			path = Sources/StravaKalcerApp;
			sourceTree = "<group>";
		}};
		{products_group_id} /* Products */ = {{
			isa = PBXGroup;
			children = (
				{product_app_id} /* StravaKalcer.app */,
			);
			name = Products;
			sourceTree = "<group>";
		}};
/* End PBXGroup section */

/* Begin PBXNativeTarget section */
		{target_id} /* StravaKalcer */ = {{
			isa = PBXNativeTarget;
			buildConfigurationList = {target_cfg_list_id} /* Build configuration list for PBXNativeTarget "StravaKalcer" */;
			buildPhases = (
				{sources_phase_id} /* Sources */,
				{frameworks_phase_id} /* Frameworks */,
				{resources_phase_id} /* Resources */,
			);
			buildRules = (
			);
			dependencies = (
			);
			name = StravaKalcer;
			productName = StravaKalcer;
			productReference = {product_app_id} /* StravaKalcer.app */;
			productType = "com.apple.product-type.application";
		}};
/* End PBXNativeTarget section */

/* Begin PBXProject section */
		{project_id} /* Project object */ = {{
			isa = PBXProject;
			attributes = {{
				BuildIndependentTargetsInParallel = 1;
				LastUpgradeCheck = 1520;
				TargetAttributes = {{
					{target_id} = {{
						CreatedOnToolsVersion = 15.2;
					}};
				}};
			}};
			buildConfigurationList = {proj_cfg_list_id} /* Build configuration list for PBXProject "StravaKalcer" */;
			compatibilityVersion = "Xcode 14.0";
			developmentRegion = en;
			hasScannedForEncodings = 0;
			knownRegions = (
				en,
				Base,
			);
			mainGroup = {root_group_id};
			productRefGroup = {products_group_id} /* Products */;
			projectDirPath = "";
			projectRoot = "";
			targets = (
				{target_id} /* StravaKalcer */,
			);
		}};
/* End PBXProject section */

/* Begin PBXResourcesBuildPhase section */
		{resources_phase_id} /* Resources */ = {{
			isa = PBXResourcesBuildPhase;
			buildActionMask = 2147483647;
			files = (
				{sample_gpx_build_id} /* sample_loop.gpx in Resources */,
				{assets_build_id} /* Assets.xcassets in Resources */,
			);
			runOnlyForDeploymentPostprocessing = 0;
		}};
/* End PBXResourcesBuildPhase section */

/* Begin PBXSourcesBuildPhase section */
		{sources_phase_id} /* Sources */ = {{
			isa = PBXSourcesBuildPhase;
			buildActionMask = 2147483647;
			files = (
"""

for f in core_files + app_files:
    pbx += f"\t\t\t\t{build_files[f]} /* {f} in Sources */,\n"

pbx += f"""			);
			runOnlyForDeploymentPostprocessing = 0;
		}};
/* End PBXSourcesBuildPhase section */

/* Begin XCBuildConfiguration section */
		{proj_debug_cfg_id} /* Debug */ = {{
			isa = XCBuildConfiguration;
			buildSettings = {{
				ALWAYS_SEARCH_USER_PATHS = NO;
				CLANG_ANALYZER_NONNULL = YES;
				CLANG_ENABLE_MODULES = YES;
				CLANG_ENABLE_OBJC_ARC = YES;
				ENABLE_TESTABILITY = YES;
				GCC_DYNAMIC_NO_PIC = NO;
				GCC_OPTIMIZATION_LEVEL = 0;
				GCC_PREPROCESSOR_DEFINITIONS = (
					"DEBUG=1",
					"$(inherited)",
				);
				IPHONEOS_DEPLOYMENT_TARGET = 17.0;
				SDKROOT = iphoneos;
				SWIFT_ACTIVE_COMPILATION_CONDITIONS = DEBUG;
				SWIFT_OPTIMIZATION_LEVEL = "-Onone";
				SWIFT_VERSION = 5.0;
			}};
			name = Debug;
		}};
		{proj_release_cfg_id} /* Release */ = {{
			isa = XCBuildConfiguration;
			buildSettings = {{
				ALWAYS_SEARCH_USER_PATHS = NO;
				CLANG_ANALYZER_NONNULL = YES;
				CLANG_ENABLE_MODULES = YES;
				CLANG_ENABLE_OBJC_ARC = YES;
				IPHONEOS_DEPLOYMENT_TARGET = 17.0;
				SDKROOT = iphoneos;
				SWIFT_COMPILATION_MODE = "wholemodule";
				SWIFT_OPTIMIZATION_LEVEL = "-O";
				SWIFT_VERSION = 5.0;
			}};
			name = Release;
		}};
		{target_debug_cfg_id} /* Debug */ = {{
			isa = XCBuildConfiguration;
			buildSettings = {{
				ASSETCATALOG_COMPILER_APPICON_NAME = AppIcon;
				CODE_SIGN_STYLE = Automatic;
				CURRENT_PROJECT_VERSION = 1;
				DEVELOPMENT_TEAM = KMXN9J6LFF;
				GENERATE_INFOPLIST_FILE = NO;
				INFOPLIST_FILE = Sources/StravaKalcerApp/Info.plist;
				LD_RUNPATH_SEARCH_PATHS = (
					"$(inherited)",
					"@executable_path/Frameworks",
				);
				MARKETING_VERSION = 1.0;
				PRODUCT_BUNDLE_IDENTIFIER = com.ardianrifendy.StravaKalcer;
				PRODUCT_NAME = "$(TARGET_NAME)";
				SWIFT_EMIT_LOC_STRINGS = YES;
				TARGETED_DEVICE_FAMILY = "1,2";
			}};
			name = Debug;
		}};
		{target_release_cfg_id} /* Release */ = {{
			isa = XCBuildConfiguration;
			buildSettings = {{
				ASSETCATALOG_COMPILER_APPICON_NAME = AppIcon;
				CODE_SIGN_STYLE = Automatic;
				CURRENT_PROJECT_VERSION = 1;
				DEVELOPMENT_TEAM = KMXN9J6LFF;
				GENERATE_INFOPLIST_FILE = NO;
				INFOPLIST_FILE = Sources/StravaKalcerApp/Info.plist;
				LD_RUNPATH_SEARCH_PATHS = (
					"$(inherited)",
					"@executable_path/Frameworks",
				);
				MARKETING_VERSION = 1.0;
				PRODUCT_BUNDLE_IDENTIFIER = com.ardianrifendy.StravaKalcer;
				PRODUCT_NAME = "$(TARGET_NAME)";
				SWIFT_EMIT_LOC_STRINGS = YES;
				TARGETED_DEVICE_FAMILY = "1,2";
			}};
			name = Release;
		}};
/* End XCBuildConfiguration section */

/* Begin XCConfigurationList section */
		{proj_cfg_list_id} /* Build configuration list for PBXProject "StravaKalcer" */ = {{
			isa = XCConfigurationList;
			buildConfigurations = (
				{proj_debug_cfg_id} /* Debug */,
				{proj_release_cfg_id} /* Release */,
			);
			defaultConfigurationIsVisible = 0;
			defaultConfigurationName = Release;
		}};
		{target_cfg_list_id} /* Build configuration list for PBXNativeTarget "StravaKalcer" */ = {{
			isa = XCConfigurationList;
			buildConfigurations = (
				{target_debug_cfg_id} /* Debug */,
				{target_release_cfg_id} /* Release */,
			);
			defaultConfigurationIsVisible = 0;
			defaultConfigurationName = Release;
		}};
/* End XCConfigurationList section */

	}};
	rootObject = {project_id} /* Project object */;
}}
"""

out_path = os.path.join(xcodeproj_dir, "project.pbxproj")
with open(out_path, "w") as f:
    f.write(pbx)

print(f"Generated successfully: {out_path}")
