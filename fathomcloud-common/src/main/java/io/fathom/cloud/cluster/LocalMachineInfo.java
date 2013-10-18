package io.fathom.cloud.cluster;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.fathomdb.Configuration;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

@Singleton
public class LocalMachineInfo {

    @Inject
    Configuration configuration;

    List<InetAddress> addresses;

    List<InetAddress> getAddresses() {
        // TODO: Don't cache? What if this changes?
        if (this.addresses == null) {
            List<InetAddress> addresses = Lists.newArrayList();

            Enumeration<NetworkInterface> networkInterfaces;
            try {
                networkInterfaces = NetworkInterface.getNetworkInterfaces();
            } catch (SocketException e) {
                throw new IllegalStateException("Error listing network interfaces", e);
            }

            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface networkInterface = networkInterfaces.nextElement();

                List<InterfaceAddress> interfaceAddresses = networkInterface.getInterfaceAddresses();
                for (InterfaceAddress interfaceAddress : interfaceAddresses) {
                    InetAddress address = interfaceAddress.getAddress();
                    addresses.add(address);
                }
            }

            this.addresses = addresses;
        }

        return this.addresses;
    }

    String machineKey;

    public String getMachineKey() {
        if (machineKey == null) {
            File keyPath = configuration.lookupFile("machine.uniquekey.file", "/var/openstack/machineid");

            if (!keyPath.exists()) {
                String value = new MachineSignature().generateKey();
                if (!keyPath.exists()) {
                    // TODO: Can we do this only-if-not-exists?
                    try {
                        Files.write(value.getBytes(Charsets.UTF_8), keyPath);
                    } catch (IOException e) {
                        throw new IllegalStateException("Error writing machine key file", e);
                    }
                }
            }

            try {
                this.machineKey = Files.toString(keyPath, Charsets.UTF_8);
            } catch (IOException e) {
                throw new IllegalStateException("Error reading machine key file", e);
            }
        }

        return machineKey;
    }

}
