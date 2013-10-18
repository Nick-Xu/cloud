package io.fathom.cloud.compute.networks;

import java.net.Inet6Address;
import java.net.InetAddress;

public class IpV6Range extends IpRange {

    public IpV6Range(Inet6Address address, int netmaskLength) {
        super(address, netmaskLength);
    }

    @Override
    public boolean contains(InetAddress address) {
        if (address instanceof Inet6Address) {
            return super.contains(address);
        } else {
            return false;
        }
    }

    // public boolean overlaps(IpV6Range cidr) {
    // if (this.netmaskLength != cidr.netmaskLength) {
    // throw new UnsupportedOperationException("Not implemented");
    // }
    //
    // byte[] thisMask = getMasked();
    // byte[] otherMask = cidr.getMasked();
    //
    // return Arrays.equals(thisMask, otherMask);
    // }
    //
    // public Iterable<IpV6Range> listSubnets(int bitCount) {
    // int count = 1 << bitCount;
    //
    // // TODO: This is outrageously inefficient
    // List<IpV6Range> items = Lists.newArrayList();
    // byte[] addr = getMasked();
    // for (int i = 0; i < count; i++) {
    // addBit(addr, bitCount + netmaskLength - 1);
    // Inet6Address subAddress = (Inet6Address) toAddress(addr);
    // items.add(new IpV6Range(subAddress, bitCount + netmaskLength));
    // }
    //
    // return items;
    // }

    // @Override
    // public String getNetmask() {
    // return String.valueOf(netmaskLength);
    // }

}
