package com.fitlogtimer.controller;

import com.fitlogtimer.service.ImportService;
import com.fitlogtimer.service.XlsxService;
import com.fitlogtimer.util.ScriptExecutor;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@AllArgsConstructor
public class ImportController {

    private XlsxService xlsxService;
    private ImportService importService;

    @GetMapping("/run-import-script")
    public String runScript(Model model) {
        ScriptExecutor.runPowerShellScript("scripts\\downloadFromDrive.ps1");

        model.addAttribute("message", "Le script d'import xls a été exécuté.");
        return "import";
    }

    @GetMapping("/drive-xls")
    public String showImportPage(Model model) throws IOException {
        model.addAttribute("sheets", xlsxService.listImportableSheetNames());
        return "import";
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
