
#ifndef IPV6SOCK
#define IPV6SOCK
/*********************************************************************
 *
 * Authors: Vincenzo Ciaschini - Vincenzo.Ciaschini@cnaf.infn.it 
 *
 * Copyright (c) 2002-2009 INFN-CNAF on behalf of the EU DataGrid
 * and EGEE I, II and III
 * For license conditions see LICENSE file or
 * http://www.apache.org/licenses/LICENSE-2.0.txt
 *
 * Parts of this code may be based upon or even include verbatim pieces,
 * originally written by other people, in which case the original header
 * follows.
 *
 *********************************************************************/

extern int bind_and_listen(char* port, int backlog, void *logh);
extern int accept_ipv6(int sock, void *logh);
extern int sock_connect(const char *host, char *port, void *logh);

#endif