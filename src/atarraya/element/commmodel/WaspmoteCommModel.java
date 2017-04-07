/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package atarraya.element.commmodel;

import atarraya.constants;

/**
 *
 * @author pwightman
 */
public class WaspmoteCommModel implements constants, CommModel{

    double lambda, lp, ptx, prx, N, cn, fbbw, ebno=0;
    
    
        // Implements the Gauss error function.
    // erf(z) = 2 / sqrt(pi) * integral(exp(-t*t), t = 0..z)
    // Copyright � 2007, Robert Sedgewick and Kevin Wayne.
    // Last updated: Tue Sep 29 16:17:41 EDT 2009.
    // http://www.cs.princeton.edu/introcs/21function/ErrorFunction.java.html

    // fractional error in math formula less than 1.2 * 10 ^ -7.
    // although subject to catastrophic cancellation when z in very close to 0
    // from Chebyshev fitting formula for erf(z) from Numerical Recipes, 6.2
    public static double erf(double z) {
        double t = 1.0 / (1.0 + 0.5 * Math.abs(z));

        // use Horner's method
        double ans = 1 - t * Math.exp( -z*z   -   1.26551223 +
                                            t * ( 1.00002368 +
                                            t * ( 0.37409196 +
                                            t * ( 0.09678418 +
                                            t * (-0.18628806 +
                                            t * ( 0.27886807 +
                                            t * (-1.13520398 +
                                            t * ( 1.48851587 +
                                            t * (-0.82215223 +
                                            t * ( 0.17087277))))))))));
        if (z >= 0) return  ans;
        else        return -ans;
    }

    // Gaussian Q-function
    // Check document "Q function and error function"
    // http://www.eng.tau.ac.il/~jo/academic/Q.pdf
    public static double Q(double x) {
        return 0.5 * (1 - erf(x / Math.sqrt(2)));
    }
    
    
    public double calcBER(double distance){
    
        lambda = 299792458 / 2.4E9; // lambda = c / f; f: 2.4 GHz for ZigBee
        // Open air path loss, alpha = 2
        lp = 22 + 20*Math.log10(distance / lambda);
        ptx = 10*Math.log10(2); // Ptx = 2 mW (2 dBi antenna)
        // rx power = tx power - lp - fm, where
        // fm: fade margin (9 dB, used normally for 2.4 GHz radios)
        prx = ptx - lp - 9;
        // N = k*T*B, where:
        // k: Boltzmann constant (1.38065*10^-23 m^2*kg*s^-2*K^-1)
        // T: effective temperature in Kelvin (290 K)
        // B: System bandwidth (5 MHz)
        N = 10*Math.log10(1.3806503E-23 * 290 * 5E6 / 1E-3); // Divided by 1 mW
        // Carrier to noise ratio: C/N (prx - N, in dB)
        cn = prx - N;
        // fb/bw = 10*log(bitrate / bandwidth), bitrate: 38400 bps
        fbbw = 10*Math.log10(38400 / 5E6);
        // Eb/N0 = C/N + fb/bw 
        ebno = cn + fbbw;
        // BER for OQPSK is the same as QPSK
        // check section 8.1.1.5 of the book
        // "Digital Communication over Fading Channels: A Unified Approach to Performance Analysis"
        // by Marvin K. Simon and Mohamed-Slim Alouini. John Wiley & Sons, Inc., 2000
        double bit_error_rate = Q(Math.sqrt(2 * Math.pow(10, ebno/10))); // Equation (8.18)
        
        return bit_error_rate;
    }
    
}
