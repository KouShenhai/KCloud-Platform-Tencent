#!/bin/bash

#参数
project_path="/opt/laokou"
admin="admin"
gateway="gateway"
register="register"
auth="auth"

#运行
run() {
  # shellcheck disable=SC2164
  cd ${project_path}

  echo 'remove file...'
  # shellcheck disable=SC2035
  sudo rm -rf *.jar

  echo 'copy file...'
  sudo cp /var/lib/jenkins/workspace/kcloud-platform/laokou-service/laokou-${admin}/target/${admin}.jar ${project_path}
  sudo cp /var/lib/jenkins/workspace/kcloud-platform/laokou-cloud/laokou-${gateway}/target/${gateway}.jar ${project_path}
  sudo cp /var/lib/jenkins/workspace/kcloud-platform/laokou-cloud/laokou-${register}/target/${register}.jar ${project_path}
  sudo cp /var/lib/jenkins/workspace/kcloud-platform/laokou-service/laokou-${auth}/target/${auth}.jar ${project_path}

  echo "run ${admin}..."
  sudo sh laokou-${admin}.sh

  echo "run ${gateway}..."
  sudo sh laokou-${gateway}.sh

  echo "run ${register}..."
  sudo sh laokou-${register}.sh

  echo "run ${auth}..."
  sudo sh laokou-${auth}.sh
}

#打包
build() {
  echo "stop ${admin}..."
  # shellcheck disable=SC2006
  # shellcheck disable=SC2009
  admin_pid=`ps -ef|grep ${admin}.jar|grep -v grep|awk '{print $2}'`
  if [ -z "${admin_pid}" ]; then
      echo "${admin} is already stopped..."
  else
       sudo kill "${admin_pid}"
  fi

  echo "stop ${gateway}..."
  # shellcheck disable=SC2006
  # shellcheck disable=SC2009
  gateway_pid=`ps -ef|grep ${gateway}.jar|grep -v grep|awk '{print $2}'`
  if [ -z "${gateway_pid}" ]; then
      echo "${gateway} is already stopped..."
  else
       sudo kill "${gateway_pid}"
  fi

  echo "stop ${register}..."
  # shellcheck disable=SC2006
  # shellcheck disable=SC2009
  register_pid=`ps -ef|grep ${register}.jar|grep -v grep|awk '{print $2}'`
  if [ -z "${register_pid}" ]; then
      echo "${register} is already stopped..."
  else
       sudo kill  "${register_pid}"
  fi

  echo "stop ${auth}..."
  # shellcheck disable=SC2006
  # shellcheck disable=SC2009
  auth_pid=`ps -ef|grep ${auth}.jar|grep -v grep|awk '{print $2}'`
  if [ -z "${auth_pid}" ]; then
      echo "${auth} is already stopped..."
  else
       # shellcheck disable=SC2086
       sudo kill  ${auth_pid}
  fi

}

if [ "$1" == "run" ]; then
    run
else
    build
fi