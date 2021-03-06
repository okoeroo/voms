/*********************************************************************
 *
 * Authors: Vincenzo Ciaschini - Vincenzo.Ciaschini@cnaf.infn.it 
 *
 * Copyright (c) Members of the EGEE Collaboration. 2004-2010.
 * See http://www.eu-egee.org/partners/ for details on the copyright holders.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Parts of this code may be based upon or even include verbatim pieces,
 * originally written by other people, in which case the original header
 * follows.
 *
 *********************************************************************/

#include "replace.h"

#include <sys/types.h>
#include <string.h>
#include <stdlib.h>


#include <openssl/asn1.h>
#include <openssl/x509.h>
#include <openssl/evp.h>
#include <openssl/bn.h>
#include <openssl/bio.h>

#include "credentials.h"
#include "sslutils.h"

int
globus(int version)
{
  if (version == 0) {
    char *gver = getenv("GLOBUS_VERSION");

    if (gver) {
      char *tmp;
      
      version = strtol(gver, &tmp, 10);
      if (!(*tmp))
        return 22;
    }
  }
  
  if (version >= 42 || version == 0)
    version = 22;

  return version;
}



X509 *
get_real_cert(X509 *base, STACK_OF(X509) *stk)
{
  X509 *cert = NULL;
  int i;

  if (!proxy_check_proxy_name(base))
    return base;

  /* Determine id data */
  for (i = 0; i < sk_X509_num(stk); i++) {
    cert = sk_X509_value(stk, i);
    if (!proxy_check_proxy_name(cert)) {
      return cert;
    }
  }
  return NULL;
}

char *
get_peer_serial(X509 *cert)
{
  char *res = NULL;
  ASN1_INTEGER * tmp;

  if (!cert)
    return NULL;

  tmp = X509_get_serialNumber(cert);
  if (tmp) 
  {
    BIGNUM *bn = ASN1_INTEGER_to_BN(tmp, NULL);
    if (bn)
      res = BN_bn2hex(bn);
    BN_free(bn);
  }
  
  return res;
}
 
