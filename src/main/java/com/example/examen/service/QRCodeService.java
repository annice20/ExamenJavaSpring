package com.example.examen.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;

import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Path;

import com.google.zxing.client.j2se.MatrixToImageWriter;

@Service
public class QRCodeService {

	public void genererQRCode(String texte, String chemin) throws Exception {
	
		QRCodeWriter writer = new QRCodeWriter();
		
		var matrix = writer.encode(texte, BarcodeFormat.QR_CODE, 250, 250);
		
		Path path = FileSystems.getDefault().getPath(chemin);
		
		MatrixToImageWriter.writeToPath(matrix,"PNG",path);
	
	}

}