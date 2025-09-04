package com.BIProject.Laborex.Entity.IMPORTATION;

import java.time.Instant;
import java.time.LocalDateTime;

public class ImportResult {
	private String fileName;
    private long fileSize;
    private String importer;   //Nom de l'importer
    private String sheet;      // Feuille excel traité
    private int priority;      // getPriorite() de l'importer
    private Instant startedAt;
    private Instant finishedAt;
    private long durationMs;
    private String status;     // "SUCCESS" ou "FAILED"
    private String message;    // message lisible pour le front
	public ImportResult() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ImportResult(String fileName, long fileSize, String importer, String sheet, int priority, Instant startedAt,
			Instant finishedAt, long durationMs, String status, String message) {
		super();
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.importer = importer;
		this.sheet = sheet;
		this.priority = priority;
		this.startedAt = startedAt;
		this.finishedAt = finishedAt;
		this.durationMs = durationMs;
		this.status = status;
		this.message = message;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public long getFileSize() {
		return fileSize;
	}
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}
	public String getImporter() {
		return importer;
	}
	public void setImporter(String importer) {
		this.importer = importer;
	}
	public String getSheet() {
		return sheet;
	}
	public void setSheet(String sheet) {
		this.sheet = sheet;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
	public Instant getStartedAt() {
		return startedAt;
	}
	public void setStartedAt(Instant startedAt) {
		this.startedAt = startedAt;
	}
	public Instant getFinishedAt() {
		return finishedAt;
	}
	public void setFinishedAt(Instant finishedAt) {
		this.finishedAt = finishedAt;
	}
	public long getDurationMs() {
		return durationMs;
	}
	public void setDurationMs(long durationMs) {
		this.durationMs = durationMs;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}