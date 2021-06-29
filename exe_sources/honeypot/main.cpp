#ifndef UNICODE
#define UNICODE
#endif

#define WIN32_LEAN_AND_MEAN
#define _WINSOCK_DEPRECATED_NO_WARNINGS

#include <stdio.h>
#include <stdlib.h>
#include <winsock2.h>
#include <ws2tcpip.h>
#include <windows.h>
#include <iostream>
#include <fstream>
#include <string>


using namespace std;


int main(int argc, char *argv[])
{

	//----------------------
	// Initialize Winsock

	WSADATA wsaData;
	int iResult = 0;

	SOCKET ListenSocket = INVALID_SOCKET;
	sockaddr_in service;

	iResult = WSAStartup(MAKEWORD(2, 2), &wsaData);
	if (iResult != NO_ERROR) {
		printf("WSAStartup() failed with error: %d\n", iResult);
		return 0;
	}
	//----------------------
	// Create a SOCKET for listening for incoming connection requests.
	ListenSocket = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
	if (ListenSocket == INVALID_SOCKET) {
		printf("socket function failed with error: %d\n", WSAGetLastError());
		WSACleanup();
		return 0;
	}
	//----------------------
	// The sockaddr_in structure specifies the address family,
	// IP address, and port for the socket that is being bound.
	service.sin_family = AF_INET;
	service.sin_addr.s_addr = inet_addr("127.0.0.1");
	service.sin_port = htons(atoi(argv[1]));

	iResult = bind(ListenSocket, (SOCKADDR *)& service, sizeof(service));
	if (iResult == SOCKET_ERROR) {
		printf("bind function failed with error %d\n", WSAGetLastError());
		iResult = closesocket(ListenSocket);
		if (iResult == SOCKET_ERROR)
			printf("closesocket function failed with error %d\n", WSAGetLastError());
		WSACleanup();
		return 0;
	}

	//----------------------
	// Listen for incoming connection requests
	// on the created socket

	if (listen(ListenSocket, SOMAXCONN) == SOCKET_ERROR)
		printf("listen function failed with error: %d\n", WSAGetLastError());

	printf("Listening on socket...\n");

	while (true)
	{
		SOCKET AcceptSocket;
		printf("Waiting for client to connect...\n");

		SOCKADDR_IN addr;
		int addrlen = sizeof(addr);

		//----------------------
		// Accept the connection.

		AcceptSocket = accept(ListenSocket, (SOCKADDR*)&addr, &addrlen);
		if (AcceptSocket == INVALID_SOCKET) {
			printf("accept failed with error: %d\n", WSAGetLastError());
			closesocket(ListenSocket);
			WSACleanup();
			return 0;
		}
		else
		{
			ofstream myfile;
			myfile.open("honeypotlogs.txt", ios::app);
			myfile << string(inet_ntoa(addr.sin_addr)) + "\n";
			myfile.close();
			printf("Client connected from: %s\n", inet_ntoa(addr.sin_addr));
		}
	}

	iResult = closesocket(ListenSocket);
	if (iResult == SOCKET_ERROR) {
		printf("closesocket function failed with error %d\n", WSAGetLastError());
		WSACleanup();
	}

    return 0;
}
