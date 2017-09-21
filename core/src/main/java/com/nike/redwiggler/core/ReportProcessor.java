package com.nike.redwiggler.core;

import com.nike.redwiggler.core.models.RedwigglerReport;

import java.util.List;

public interface ReportProcessor {

    void process(List<RedwigglerReport> reports);

    default ReportProcessor andThen(ReportProcessor reportProcessor) {
        ReportProcessor thisProcessor = this;
        return reports -> {
            thisProcessor.process(reports);
            reportProcessor.process(reports);
        };
    }
}
