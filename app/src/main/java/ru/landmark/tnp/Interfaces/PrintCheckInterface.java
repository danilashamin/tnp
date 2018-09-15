package ru.landmark.tnp.Interfaces;

/**
 * Created by gameb on 18.02.2017.
 */

public interface PrintCheckInterface
{
    void printCheckInHire(String barcode, String Date, String FIO, String deposit, String timeStart);
    void printCheckStop(String summPrint, int summDop, String datePrint, String fioPrint, String holdingTimePrint);
}
