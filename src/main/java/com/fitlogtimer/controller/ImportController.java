package com.fitlogtimer.controller;

import com.fitlogtimer.dto.time.RelativeTimeDTO;
import com.fitlogtimer.service.ImportService;
import com.fitlogtimer.service.XlsxService;
import com.fitlogtimer.util.RelativeTimeFormatter;
import com.fitlogtimer.util.ScriptExecutor;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDateTime;

@Controller
@AllArgsConstructor
public class ImportController {

    private XlsxService xlsxService;
    private ImportService importService;

    @GetMapping("/drive-xls")
    public String showImportPage(Model model) throws IOException {
        LocalDateTime lastDownload = xlsxService.getLastDownloadTime();
        RelativeTimeDTO relativeTimeDTO = RelativeTimeFormatter.format(lastDownload);

        model.addAttribute("sheets", xlsxService.listImportableSheetNames());
        model.addAttribute("lastDownloadText", relativeTimeDTO.text());
        model.addAttribute("lastDownloadStatus", relativeTimeDTO.status());
        return "import";
    }

    @PostMapping("/run-import-script")
    public String downloadDrive(RedirectAttributes redirectAttributes) {
        ScriptExecutor.runPowerShellScript("scripts\\downloadFromDrive.ps1");

        return "redirect:/drive-xls";
    }

    @PostMapping("/run-import")
    public String runImport(@RequestParam("sheetName") String sheetName, RedirectAttributes redirectAttributes) {
        String resultMessage = importService.importSheet(sheetName);
        redirectAttributes.addFlashAttribute("message", resultMessage);
        return "redirect:/drive-xls";
    }

    @PostMapping("/run-partial-import")
    public String runPartialImport(@RequestParam("sheetName") String sheetName, RedirectAttributes redirectAttributes) {
        String resultMessage = importService.importPartialSheet(sheetName);
        redirectAttributes.addFlashAttribute("message", resultMessage);
        return "redirect:/drive-xls";
    }
}
