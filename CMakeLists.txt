cmake_minimum_required(VERSION 2.6)
# Override prior to project to build MSVC with static runtime
set(CMAKE_USER_MAKE_RULES_OVERRIDE     ${CMAKE_CURRENT_SOURCE_DIR}/../infr_scripts_pl/Build/c_flag_overrides.cmake)
set(CMAKE_USER_MAKE_RULES_OVERRIDE_CXX ${CMAKE_CURRENT_SOURCE_DIR}/../infr_scripts_pl/Build/cxx_flag_overrides.cmake)
project(tools_xta)
include("${PROJECT_SOURCE_DIR}/../infr_scripts_pl/Build/cmakerules")
add_subdirectory(com.xmos.xta.core/frontends/xta   )
