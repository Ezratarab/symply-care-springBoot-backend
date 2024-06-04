package com.example.symply_care.controller;


import com.example.symply_care.service.EmailSendService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/email")
public class EmailSendController {

    String message = "this is a message";

    private EmailSendService emailSendService;

    public EmailSendController(EmailSendService emailSendService) {
        this.emailSendService = emailSendService;
    }

    @RequestMapping("/form")
    public String emailForm(Model model) {
        model.addAttribute("message", message);
        return "index";
    }

    @Transactional
    @PostMapping("/send")
    public String sendEmail(@RequestParam("to") String to, @RequestParam("subject") String subject, @RequestParam("body") String body, RedirectAttributes redirectAttributes) {
        String response = emailSendService.sendEmail(to, subject, body);

        if (redirectAttributes != null) {
            if (response.equals("Email sent successfully")) {
                redirectAttributes.addFlashAttribute("successMessage", "Email sent successfully");
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to send email: " + response);
            }
        }

        return "redirect:/email/form";
    }




}
