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
                if not src:
                    out_file.write(u"\n")
                    continue
                if src.startswith("#"):
                    out_file.write(u"{0}\n".format(src))
                    continue
                print(src)
                (network_address, mask) = src.split("/")
                whois = IPWhois(network_address)
                whois_data = whois.lookup_rdap(depth = 1)
                network_name = whois_data["network"]["name"]
                asn = whois_data["asn"]
                asn_description = whois_data["asn_description"]
                hosts = ipaddr.IPv4Network(src).numhosts
                total_hosts += hosts
                out_file.write(u"# {0}\n# AS{1} {2}\n# {3} host(s)\n{4}\n\n".format(network_name, asn, asn_description, hosts, src))
            out_file.write(u"# {0} host(s)\n".format(total_hosts))


if __name__ == '__main__':
    main()
