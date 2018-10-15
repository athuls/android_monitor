kill $(ps aux | grep '[T]heater' | awk '{print $2}')
kill $(ps aux | grep '[s]alsa_heat.jar:. -Dnetif=eth0 wwc.naming.WWCNamingServer' | awk '{print $2}')
kill $(ps aux | grep '[s]alsa_heat.jar:.' | awk '{print $2}')
