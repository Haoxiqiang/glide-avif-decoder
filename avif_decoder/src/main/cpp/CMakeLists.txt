cmake_minimum_required(VERSION 3.22.1)

project(avif_android_jni)

set(CMAKE_VERBOSE_MAKEFILE ON)

set(CMAKE_C_FLAGS "${CMAKE_C_FLAGS} -D__STDC_FORMAT_MACROS")
set(CMAKE_SHARED_LINKER_FLAGS "${CMAKE_SHARED_LINKER_FLAGS} -O3 -flto -Wl,--gc-sections -Wl,--version-script=${CMAKE_CURRENT_SOURCE_DIR}/version_script.txt")

set(DAV1D_DIR ${CMAKE_CURRENT_SOURCE_DIR}/dav1d)

# do not use dav1d as library name, libavif condition use NOT TARGET dav1d
add_library(libdav1d STATIC IMPORTED)
set_target_properties(libdav1d PROPERTIES IMPORTED_LOCATION ${DAV1D_DIR}/${ANDROID_ABI}/libdav1d.a)
set(DAV1D_INCLUDE_DIR ${DAV1D_DIR}/include)
set(DAV1D_VERSION 1.0.0)
set(DAV1D_LIBRARY libdav1d)

# To override the variables in the libavif subproject:
# https://stackoverflow.com/questions/3766740/overriding-a-default-option-value-in-cmake-from-a-parent-cmakelists-txt
set(BUILD_SHARED_LIBS OFF CACHE BOOL "" FORCE)

set(AVIF_LOCAL_DAV1D OFF CACHE BOOL "" FORCE)
set(AVIF_CODEC_DAV1D ON CACHE BOOL "" FORCE)

add_subdirectory(../../../../external/libavif build)

set(SRC_FILES
        avif_decoder_jni.c
        avif_image_jni.c
        avif_util.c)

add_library("avif_android" SHARED ${SRC_FILES})

include(AndroidNdkModules)
android_ndk_import_module_cpufeatures()

target_link_libraries(avif_android jnigraphics avif log cpufeatures)
