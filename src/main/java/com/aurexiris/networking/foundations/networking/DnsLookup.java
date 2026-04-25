package com.aurexiris.networking.foundations.networking;


import java.net.InetAddress;
import java.net.UnknownHostException;

public class DnsLookup {



    public static void main(String[] args) {

        String domain ="google.com";


        try {
            // Perform DNS lookup
            InetAddress address = InetAddress.getByName(domain);

            //System.out.println(address);>> it will print the hostname and the IP address in the format: hostname/IP address

            System.out.println("IP Address for " + domain + " is: " + address.getHostAddress());// it will print only the IP address of the domain
        }

        catch (UnknownHostException e) {
            System.out.println("Unable to resolve host: " + e.getMessage());
        }

    }
}