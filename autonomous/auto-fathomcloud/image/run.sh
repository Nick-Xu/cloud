#!/bin/bash

#================================================
# Volumes
#================================================
# We can't create these in advance because they're on a volume
chmod 755 /volumes/persistent
chmod 755 /volumes/ephemeral

mkdir -p /volumes/persistent/fathomcloud
chown -R fathomcloud /var/persistent/fathomcloud/
ln -s /volumes/persistent/fathomcloud /var/fathomcloud

mkdir -p /volumes/persistent/zookeeper
chown -R zk /var/persistent/zookeeper/
ln -s /volumes/persistent/zookeeper /var/zookeeper

mkdir -p /volumes/ephemeral/fathomcloud/cache
ln -s /volumes/ephemeral/fathomcloud/cache /var/fathomcloud/cache

mkdir -p /volumes/persistent/ssh
chown -R fathomcloud /volumes/persistent/ssh
chmod 700 /volumes/data/ssh
ln -s /volumes/persistent/ssh /home/fathomcloud/.ssh


#================================================
# Networking
#================================================
# We can't create this tunnel in advance because it changes
HOST=`ip -6 route | grep default | cut -d ' ' -f 3`
ME=`ip addr show dev eth0 | grep global | sed -e's/^.*inet6 \([^ ]*\)\/.*$/\1/;t;d'`

sudo mkdir -p /etc/apply.d/vips/
sudo mkdir -p /etc/apply.d/viptunnel/

sudo tee /etc/apply.d/tunnel/viptunnel <<EOF
ip6ip6 local ${ME} remote ${HOST}
EOF

sudo tee /etc/apply.d/vips/fd00::c10d <<EOF
viptunnel fd00::c10d/128
EOF

sudo tee /etc/apply.d/vips/fd00::feed <<EOF
viptunnel fd00::feed/128
EOF


# Apply the tunnel rule and any other configuration rules that may have changed
sudo /usr/sbin/applyd


#================================================
# Launch manager
#================================================

# Start the manager (which starts FathomCloud + Zookeeper)
mkdir -p /var/manager
cd /var/manager

java -Dorg.slf4j.simpleLogger.defaultLogLevel=debug -cp "/opt/manager/lib/*" io.fathom.auto.AutonomousFathomCloud  >> /var/manager/output.log 2>&1
