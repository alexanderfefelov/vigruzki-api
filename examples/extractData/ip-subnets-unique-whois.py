#!/usr/bin/env python

import io
import ipaddr
from ipwhois import IPWhois
import os

def main():
    home = os.path.dirname(os.path.abspath(__file__))
    os.chdir(home)

    with io.open("ip-subnets-unique.txt") as in_file:
        lines = in_file.readlines()
        with io.open("ip-subnets-unique-whois.txt", "w") as out_file:
            for line in lines:
                src = line.strip()
                print src
                (network_address, mask) = src.split("/")
                whois = IPWhois(network_address)
                whois_data = whois.lookup_rdap(depth = 1)
                network_name = whois_data["network"]["name"]
                hosts = ipaddr.IPv4Network(src).numhosts
                out_file.write("# " + network_name + "\n")
                out_file.write(u"# " + str(hosts) + " host(s)\n")
                out_file.write(src + "\n\n")

if __name__ == '__main__':
    main()
