package com.los.loanoriginatingsystem.report.service;

import com.los.loanoriginatingsystem.report.entity.ReportFolder;
import com.los.loanoriginatingsystem.report.repository.ReportFolderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportFolderService {

    private final ReportFolderRepository repository;

    public List<ReportFolder> getAll() {
        return repository.findAll();
    }

    public ReportFolder create(String name, String description) {

        ReportFolder folder = new ReportFolder();

        folder.setId(UUID.randomUUID().toString());
        folder.setName(name);
        folder.setDescription(description);
        folder.setIsSystemFolder(false);
        folder.setCreatedAt(LocalDateTime.now());

        return repository.save(folder);
    }

    public void delete(String id) {

        ReportFolder folder =
                repository.findById(id)
                        .orElseThrow(
                                () -> new RuntimeException("Folder not found")
                        );

        if (Boolean.TRUE.equals(folder.getIsSystemFolder())) {
            throw new RuntimeException(
                    "System folders cannot be deleted"
            );
        }

        repository.delete(folder);
    }
}
