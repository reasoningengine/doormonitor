#include "stdio.h"
#include "winsock2.h"
#include <string>
#include <iostream>
#include <fstream>

#define HAVE_REMOTE
#include "pcap.h"

using namespace std;

void ProcessPacket(u_char*, int);

void print_ethernet_header(u_char*);
void PrintIpHeader(u_char*, int);
void PrintIcmpPacket(u_char*, int);
void print_udp_packet(u_char*, int);
void PrintTcpPacket(u_char*, int);
void PrintData(u_char*, int);


typedef struct ethernet_header
{
	UCHAR dest[6];
	UCHAR source[6];
	USHORT type;
}   ETHER_HDR, *PETHER_HDR, FAR * LPETHER_HDR, ETHERHeader;


typedef struct ip_hdr
{
	unsigned char ip_header_len : 4;
	unsigned char ip_version : 4;
	unsigned char ip_tos;
	unsigned short ip_total_length;
	unsigned short ip_id;

	unsigned char ip_frag_offset : 5;

	unsigned char ip_more_fragment : 1;
	unsigned char ip_dont_fragment : 1;
	unsigned char ip_reserved_zero : 1;

	unsigned char ip_frag_offset1;

	unsigned char ip_ttl;
	unsigned char ip_protocol;
	unsigned short ip_checksum;
	unsigned int ip_srcaddr;
	unsigned int ip_destaddr;
} IPV4_HDR;

//UDP header
typedef struct udp_hdr
{
	unsigned short source_port;
	unsigned short dest_port;
	unsigned short udp_length;
	unsigned short udp_checksum;
} UDP_HDR;

// TCP header
typedef struct tcp_header
{
	unsigned short source_port;
	unsigned short dest_port;
	unsigned int sequence;
	unsigned int acknowledge;

	unsigned char ns : 1;
	unsigned char reserved_part1 : 3;
	unsigned char data_offset : 4;

	unsigned char fin : 1;
	unsigned char syn : 1;
	unsigned char rst : 1;
	unsigned char psh : 1;
	unsigned char ack : 1;
	unsigned char urg : 1;

	unsigned char ecn : 1;
	unsigned char cwr : 1;



	unsigned short window;
	unsigned short checksum;
	unsigned short urgent_pointer;
} TCP_HDR;

typedef struct icmp_hdr
{
	BYTE type;
	BYTE code;
	USHORT checksum;
	USHORT id;
	USHORT seq;
} ICMP_HDR;



int tcp = 0, udp = 0, icmp = 0, others = 0, igmp = 0, total = 0, i, j;
struct sockaddr_in source, dest;
char hex[2];

ETHER_HDR *ethhdr;
IPV4_HDR *iphdr;
TCP_HDR *tcpheader;
UDP_HDR *udpheader;
ICMP_HDR *icmpheader;
u_char *data;

int main(int argc, char *argv[])
{

	if (argc < 2)
	{
		return 0;
	}

	u_int res, inum;
	char errbuf[PCAP_ERRBUF_SIZE];
	char buffer[100];
	u_char *pkt_data;
	//time_t seconds;
	struct tm tbreak;
	pcap_if_t *alldevs, *d;
	pcap_t *fp;
	struct pcap_pkthdr *header;
	string line;


	if (pcap_findalldevs_ex(PCAP_SRC_IF_STRING, NULL, &alldevs, errbuf) == -1)
	{
		fprintf(stderr, "Error in pcap_findalldevs_ex: %s\n", errbuf);
		return -1;
	}

	for (d = alldevs; d; d = d->next)
	{
		if (strcmp(argv[1], "list") == 0)
		{
			ofstream myfile;
			myfile.open("devicelist.txt", ios::app);
			myfile << string(d->name) + " ";
			myfile.close();
		}

		if (d->description)
		{
			if (strcmp(argv[1], "list") == 0)
			{
				ofstream myfile;
				myfile.open("devicelist.txt", ios::app);
				myfile << string(d->description) + "\n";
				myfile.close();
			}
		}
		else
		{
			if (strcmp(argv[1], "list") == 0)
			{
				ofstream myfile;
				myfile.open("devicelist.txt", ios::app);
				myfile << "(No description available)\n";
				myfile.close();
			}
		}
	}

	if (strcmp(argv[1], "list") == 0)
	{
		return 0;
	}

	if (strcmp(argv[1], "device") != 0)
	{
		return 0;
	}

	if ((fp = pcap_open(argv[2],
		100,
		PCAP_OPENFLAG_PROMISCUOUS /*flags*/,
		20,
		NULL,
		errbuf)
		) == NULL)
	{
		fprintf(stderr, "\nError opening adapter\n");
		return -1;
	}


	while ((res = pcap_next_ex(fp, &header, (const u_char **)&pkt_data)) >= 0)
	{
		if (res == 0)
		{
			continue;
		}

		int iphdrlen = 0;

		iphdr = (IPV4_HDR *)(pkt_data + sizeof(ETHER_HDR));


		switch (iphdr->ip_protocol) //Check the Protocol and do accordingly...
		{
			case 1:
				line = "ICMP";
				break;

			case 2:
				line = "IGMP";
				break;

			case 6:
				line = "TCP";
				break;

			case 17:
				line = "UDP";
				break;

			default:
				line = "Other";
				break;
		}

		line = line + " - ";

		iphdrlen = iphdr->ip_header_len * 4;

		memset(&source, 0, sizeof(source));
		source.sin_addr.s_addr = iphdr->ip_srcaddr;

		memset(&dest, 0, sizeof(dest));
		dest.sin_addr.s_addr = iphdr->ip_destaddr;

		line = line + string(inet_ntoa(source.sin_addr)) + " - " + string(inet_ntoa(dest.sin_addr)) + "\n";

		ofstream myfile2;
		myfile2.open("iplogs.txt", ios::app);
		myfile2 << line;
		myfile2.close();
	}

	if (res == -1)
	{
		fprintf(stderr, "Error reading packets: %s\n", pcap_geterr(fp));
		return -1;
	}

	return 0;
}
