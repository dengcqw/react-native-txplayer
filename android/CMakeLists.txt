project("txdownloadcpp")
cmake_minimum_required(VERSION 3.9.0)

set (PACKAGE_NAME "txdownloadcpp")
set (BUILD_DIR ${CMAKE_SOURCE_DIR}/build)
set(CMAKE_VERBOSE_MAKEFILE ON)
set(CMAKE_CXX_STANDARD 17)

include_directories(
        "."
)

file (GLOB LIBRN_DIR "${BUILD_DIR}/react-native-0*/jni/${ANDROID_ABI}")
message(STATUS "LIBRN_DIR: ${LIBRN_DIR}")

  add_library(
          ${PACKAGE_NAME}
          SHARED
          ./cpp-adapter.cpp
  )


set_target_properties(
        ${PACKAGE_NAME} PROPERTIES
        CXX_STANDARD 17
        CXX_EXTENSIONS OFF
        POSITION_INDEPENDENT_CODE ON
)

find_package(ReactAndroid REQUIRED CONFIG)
find_library(log-lib log)

target_link_libraries(
        ${PACKAGE_NAME}
        ${log-lib}
        ReactAndroid::jsi
        android
)
