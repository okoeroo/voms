/*********************************************************************
 *
 * Authors: Vincenzo Ciaschini - Vincenzo.Ciaschini@cnaf.infn.it
 *
 * Copyright (c) 2002, 2003, 2004, 2005, 2006 INFN-CNAF on behalf of the 
 * EGEE project.
 * For license conditions see LICENSE
 *
 * Parts of this code may be based upon or even include verbatim pieces,
 * originally written by other people, in which case the original header
 * follows.
 *
 *********************************************************************/
package org.glite.security.voms.ac;

import org.bouncycastle.asn1.ASN1Sequence;
//import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEREncodable;
import org.bouncycastle.asn1.DEREncodableVector;
import org.bouncycastle.asn1.DERObject;
//import org.bouncycastle.asn1.DERObjectIdentifier;
//import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSequence;
//import org.bouncycastle.asn1.DERTaggedObject;
//import org.bouncycastle.asn1.DERUniversalString;
//import org.bouncycastle.asn1.x509.GeneralNames;
import org.bouncycastle.asn1.x509.X509CertificateStructure;
import org.bouncycastle.jce.provider.X509CertificateObject;

import java.security.cert.X509Certificate;

import java.util.Enumeration;
//import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.ListIterator;

//import java.security.cert.CRLException;
//import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
//import java.security.cert.CertificateParsingException;
//import java.security.cert.TrustAnchor;
//import java.security.cert.X509CRL;
//import java.security.cert.X509Certificate;

import java.io.ByteArrayInputStream;

/**
 * This class represents the ACCerts extension which may be present in the AC.
 *
 * @author Vincenzo Ciaschini.
 */
public class ACCerts implements DEREncodable {
    List l;

    /**
     * Creates an empty ACCerts object.
     */
    public ACCerts() {
        l = new Vector();
    }

    /**
     * Creates an ACCerts starting from a sequence.
     *
     * @param seq the Sequence.
     *
     * @throws IllegalArgumentException if Certificates are not supported
     * or if there is an encoding error.
     */
    public ACCerts(ASN1Sequence seq) {
        l = new Vector();
        seq = (ASN1Sequence) seq.getObjectAt(0);
        CertificateFactory cf = null;
        try {
            cf = CertificateFactory.getInstance("X.509");
        }
        catch (Exception ex) {
            throw new IllegalArgumentException("Error in setting up ACCerts reader. " + ex.getMessage());
        }

        for (Enumeration e = seq.getObjects(); e.hasMoreElements();){
            Object o = e.nextElement();
            //            System.out.println("O CLASS: " + o.getClass());
            if (o instanceof DERSequence) {
                ASN1Sequence s = ASN1Sequence.getInstance(o);
                byte[] data = null;
                try {
                    data = new X509CertificateObject(X509CertificateStructure.getInstance(s)).getEncoded();
                    l.add((X509Certificate)cf.generateCertificate(new ByteArrayInputStream(data)));
                }
                catch(Exception ex) {
                    throw new IllegalArgumentException("Error in encoding ACCerts. " + ex.getMessage());
                }

                //X509CertificateStructure.getInstance(s));
            }
            else
                throw new IllegalArgumentException("Incorrect encoding for ACCerts");
        }
    }

    /**
     * static variant of the constructor.
     *
     * @see #ACCerts(ASN1Sequence seq)
     */
    public static ACCerts getInstance(ASN1Sequence seq) {
        return new ACCerts(seq);
    }

    /**
     * Manually adds a certificate to the list.
     *
     * @param cert The certificate to add.
     */
    public void addCert(X509CertificateStructure cert) {
        l.add(cert);
    }

    /**
     * Gets the certificates.
     *
     * @return the list of certificates.
     */
    public List getCerts() {
        return l;
    }

    /**
     * Makes a DERObject representation.
     *
     * @return the DERObject
     */
    public DERObject getDERObject() {
        DEREncodableVector v = new DEREncodableVector();

        ListIterator li = l.listIterator();
        while (li.hasNext()) {
            X509CertificateStructure x509 = (X509CertificateStructure)li.next();
            v.add(x509);
        }
        return new DERSequence(v);
    }
}
