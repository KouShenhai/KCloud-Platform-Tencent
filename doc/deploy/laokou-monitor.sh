# shellcheck disable=SC2024
cd /opt/laokou && sudo nohup java -Xms256m -Xmx512m -jar monitor.jar --spring.profiles.active=prod > monitor.log &