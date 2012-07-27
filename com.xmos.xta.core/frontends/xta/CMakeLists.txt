include_directories(
  "./"
  "${PROJECT_SOURCE_DIR}/../infr_libs_java/com.xmos.libs.core/frontends/frontendlib"
  "${PROJECT_SOURCE_DIR}/../infr_libs_cpp/export/include"
)
add_executable(xta
  xta.cpp
)
target_link_libraries(xta
  ${PROJECT_SOURCE_DIR}/../infr_libs_java/${BINEXP}/frontendlib${LIBEXT}
  ${PROJECT_SOURCE_DIR}/../infr_libs_cpp/${BINEXP}/ErrorHandler${LIBEXT}
  ${PROJECT_SOURCE_DIR}/../infr_libs_cpp/${BINEXP}/StringLibrary${LIBEXT}
  ${PROJECT_SOURCE_DIR}/../infr_libs_cpp/${BINEXP}/ElfHandler${LIBEXT}
  ${PROJECT_SOURCE_DIR}/../infr_libs_cpp/${BINEXP}/XmlParser${LIBEXT}
  ${PROJECT_SOURCE_DIR}/../infr_libs_cpp/${BINEXP}/XmosBinaryHandler${LIBEXT}
)
install(TARGETS xta DESTINATION ${PROJECT_SOURCE_DIR}/${BINEXP})
