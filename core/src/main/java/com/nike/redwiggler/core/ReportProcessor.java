package com.nike.redwiggler.core;

import com.nike.redwiggler.core.models.RedwigglerReport;

import java.util.List;

public interface ReportProcessor {

    void process(List<RedwigglerReport> reports);
}
