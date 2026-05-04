package com.app.servicefinder.service;
 
import com.app.servicefinder.dto.report.ReportRequest;
import com.app.servicefinder.model.*;
import com.app.servicefinder.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
 
@Service
@RequiredArgsConstructor
public class ReportService {
 
    private final ReportRepository reportRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
 
    public void submit(Long reporterId, ReportRequest request) {
        if (reportRepository.existsByReporterIdAndServiceId(reporterId, request.getServiceId())) {
            throw new RuntimeException("Vous avez déjà signalé ce service");
        }
        User reporter = userRepository.findById(reporterId).orElseThrow();
        com.app.servicefinder.model.Service service = serviceRepository
                .findById(request.getServiceId()).orElseThrow();
 
        reportRepository.save(Report.builder()
                .reason(request.getReason())
                .reporter(reporter)
                .service(service)
                .status("PENDING")
                .build());
    }
 
    public List<Report> getPendingReports() {
        return reportRepository.findByStatus("PENDING");
    }
 
    public void resolve(Long reportId) {
        Report report = reportRepository.findById(reportId).orElseThrow();
        report.setStatus("RESOLVED");
        reportRepository.save(report);
    }
}