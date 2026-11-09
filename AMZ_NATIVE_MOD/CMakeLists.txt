cmake_minimum_required(VERSION 3.22)
project(amz_native LANGUAGES CXX)
set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_POSITION_INDEPENDENT_CODE ON)

include(FetchContent)
FetchContent_Declare(preloader_android
  GIT_REPOSITORY https://github.com/LiteLDev/preloader-android.git
  GIT_TAG 0.2.2
  SOURCE_SUBDIR cmake/preloader-sdk
)
FetchContent_MakeAvailable(preloader_android)
FetchContent_GetProperties(preloader_android)
set(LEVI_PRELOADER_INCLUDE_DIR "${preloader_android_SOURCE_DIR}/include")

if(NOT ANDROID)
  message(STATUS "AMZ native mod: configure with Android NDK for armeabi-v7a")
  return()
endif()

if(NOT CMAKE_ANDROID_ARCH_ABI STREQUAL "armeabi-v7a")
  message(FATAL_ERROR "AMZ native mod currently targets armeabi-v7a only")
endif()

if(NOT TARGET preloader)
  add_subdirectory("${preloader_android_SOURCE_DIR}" "${CMAKE_BINARY_DIR}/_preloader_android")
endif()

add_library(amz_native SHARED src/main.cpp src/AMZNativeMod.cpp src/AMZRuntimeProbe.cpp src/AMZKillAura.cpp)
target_include_directories(amz_native PRIVATE src "${LEVI_PRELOADER_INCLUDE_DIR}")
target_link_libraries(amz_native PRIVATE preloader)
target_link_options(amz_native PRIVATE "-Wl,-z,max-page-size=16384")
find_library(log-lib log REQUIRED)
find_library(dl-lib dl REQUIRED)
target_link_libraries(amz_native PRIVATE ${log-lib} ${dl-lib})

set(MOD_PACKAGE_DIR "${CMAKE_CURRENT_BINARY_DIR}/package/amz_native")
file(MAKE_DIRECTORY "${MOD_PACKAGE_DIR}")
configure_file(manifest.json.in "${CMAKE_CURRENT_BINARY_DIR}/manifest.json" @ONLY)
add_custom_command(TARGET amz_native POST_BUILD
  COMMAND "${CMAKE_COMMAND}" -E copy "$<TARGET_FILE:amz_native>" "${MOD_PACKAGE_DIR}/libamz_native.so"
  COMMAND "${CMAKE_COMMAND}" -E copy "${CMAKE_CURRENT_BINARY_DIR}/manifest.json" "${MOD_PACKAGE_DIR}/manifest.json"
)
add_custom_target(levi_package
  COMMAND "${CMAKE_COMMAND}" -E tar cf "${CMAKE_CURRENT_BINARY_DIR}/AMZ_NATIVE-armeabi-v7a.levipack" --format=zip amz_native
  WORKING_DIRECTORY "${CMAKE_CURRENT_BINARY_DIR}/package"
  DEPENDS amz_native
)
