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
            out_file.write(u"# Attention! All host counters include broadcast and network addresses.\n\n")
            total_hosts = 0
            for line in lines:
                src = line.strip()
                print src
                (network_address, mask) = src.split("/")
                whois = IPWhois(network_address)
                whois_data = whois.lookup_rdap(depth = 1)
                network_name = whois_data["network"]["name"]
                hosts = ipaddr.IPv4Network(src).numhosts
                total_hosts += hosts
                out_file.write(u"# {0}\n# {1} host(s)\n{2}\n\n".format(network_name, hosts, src))
            out_file.write(u"# {0} host(s)\n".format(total_hosts))


if __name__ == '__main__':
    main()
