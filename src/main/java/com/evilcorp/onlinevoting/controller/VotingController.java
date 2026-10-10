package com.evilcorp.onlinevoting.controller;

import java.util.UUID;

import jakarta.validation.Valid;
import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.evilcorp.onlinevoting.service.VotingService;
import com.evilcorp.onlinevoting.model.enums.VotingStatus;
import com.evilcorp.onlinevoting.service.VotingUserService;
import com.evilcorp.onlinevoting.dto.request.CastVoteRequest;
import com.evilcorp.onlinevoting.service.VotingResultsService;
import com.evilcorp.onlinevoting.dto.request.OwnerActionRequest;
import com.evilcorp.onlinevoting.exception.ApplicationException;
import com.evilcorp.onlinevoting.dto.request.CreateVotingRequest;
import com.evilcorp.onlinevoting.dto.request.UpdateVotingRequest;
import com.evilcorp.onlinevoting.exception.ResourceNotFoundException;
import com.evilcorp.onlinevoting.dto.response.CandidateOptionResponse;

@Controller
@RequiredArgsConstructor
public class VotingController {
    private final VotingService votingService;
    private final VotingUserService userService;
    private final VotingResultsService resultsService;

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ResourceNotFoundException.class)
    public String notFound(ResourceNotFoundException exception, Model model) {
        model.addAttribute("errorKey", exception.getMessageKey());
        return "error";
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("votings", votingService.listVotings());
        return "home";
    }

    @GetMapping("/votings/new")
    public String newVoting(Model model) {
        model.addAttribute("createVotingRequest", new CreateVotingRequest());
        model.addAttribute("users", userService.listUsers());
        return "votings/create";
    }

    @PostMapping("/votings")
    public String createVoting(
            Model model,
            RedirectAttributes redirectAttributes,
            @Valid @ModelAttribute("createVotingRequest") CreateVotingRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("users", userService.listUsers());
            return "votings/create";
        }

        try {
            UUID id = votingService.createVoting(request);
            redirectAttributes.addFlashAttribute("messageKey", "flash.voting.created");
            return "redirect:/votings/" + id;
        } catch (ApplicationException exception) {
            bindingResult.reject(exception.getMessageKey(), exception.getArgs(), exception.getMessageKey());
            model.addAttribute("users", userService.listUsers());
            return "votings/create";
        }
    }

    @GetMapping("/votings/{id}")
    public String details(@PathVariable UUID id, Model model) {
        populateDetails(id, model);
        return "votings/details";
    }

    @GetMapping("/votings/{id}/vote")
    public String vote(@PathVariable UUID id, Model model) {
        model.addAttribute("castVoteRequest", new CastVoteRequest());
        populateVotingPage(id, model);
        return "votings/vote";
    }

    @GetMapping("/votings/{id}/edit")
    public String editVoting(@PathVariable UUID id, Model model, RedirectAttributes redirectAttributes) {
        var voting = votingService.getDetails(id);

        if (voting.status() != VotingStatus.DRAFT) {
            redirectAttributes.addFlashAttribute("errorKey", "error.voting.edit.not-draft");
            return "redirect:/votings/" + id;
        }

        var request = UpdateVotingRequest.builder()
                .userId(voting.ownerId())
                .title(voting.title())
                .candidateNames(
                        voting.candidates().stream()
                                .map(CandidateOptionResponse::name)
                                .toList()
                )
                .build();

        model.addAttribute("updateVotingRequest", request);
        model.addAttribute("users", userService.listUsers());
        model.addAttribute("votingId", id);
        return "votings/edit";
    }

    @PostMapping("/votings/{id}/edit")
    public String updateVoting(
            @PathVariable UUID id,
            Model model,
            RedirectAttributes redirectAttributes,
            @Valid @ModelAttribute("updateVotingRequest") UpdateVotingRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("users", userService.listUsers());
            model.addAttribute("votingId", id);
            return "votings/edit";
        }

        try {
            votingService.updateVoting(request.forVoting(id));
            redirectAttributes.addFlashAttribute("messageKey", "flash.voting.updated");
            return "redirect:/votings/" + id;
        } catch (ApplicationException exception) {
            bindingResult.reject(exception.getMessageKey(), exception.getArgs(), exception.getMessageKey());
            model.addAttribute("users", userService.listUsers());
            model.addAttribute("votingId", id);
            return "votings/edit";
        }
    }

    @PostMapping("/votings/{id}/vote")
    public String castVote(
            Model model,
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes,
            @Valid @ModelAttribute("castVoteRequest") CastVoteRequest request,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            populateVotingPage(id, model);
            return "votings/vote";
        }

        try {
            votingService.castVote(request.forVoting(id));
            redirectAttributes.addFlashAttribute("messageKey", "flash.vote.accepted");
            return "redirect:/votings/" + id + "/vote";
        } catch (ApplicationException exception) {
            bindingResult.reject(exception.getMessageKey(), exception.getArgs(), exception.getMessageKey());
            populateVotingPage(id, model);
            return "votings/vote";
        }
    }

    @PostMapping("/votings/{id}/start")
    public String start(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes,
            @Valid @ModelAttribute("ownerActionRequest") OwnerActionRequest request,
            BindingResult bindingResult
    ) {
        String redirect = "redirect:/votings/" + id;

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorKey", "validation.owner.required");
            return redirect;
        }

        try {
            votingService.startVoting(request.forVoting(id));
            redirectAttributes.addFlashAttribute("messageKey", "flash.voting.started");
        } catch (ApplicationException exception) {
            redirectAttributes.addFlashAttribute("errorKey", exception.getMessageKey());
        }

        return redirect;
    }

    @PostMapping("/votings/{id}/stop")
    public String stop(
            @PathVariable UUID id,
            RedirectAttributes redirectAttributes,
            @Valid @ModelAttribute("ownerActionRequest") OwnerActionRequest request,
            BindingResult bindingResult
    ) {
        String redirect = "redirect:/votings/" + id;

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorKey", "validation.owner.required");
            return redirect;
        }

        try {
            votingService.stopVoting(request.forVoting(id));
            redirectAttributes.addFlashAttribute("messageKey", "flash.voting.stopped");
        } catch (ApplicationException exception) {
            redirectAttributes.addFlashAttribute("errorKey", exception.getMessageKey());
        }

        return redirect;
    }

    @GetMapping("/votings/{id}/results")
    public String results(@PathVariable UUID id, Model model) {
        model.addAttribute("results", resultsService.getResults(id));
        model.addAttribute("resultsUrl", votingService.getResultsUrl(id));
        return "votings/results";
    }

    private void populateDetails(UUID id, Model model) {
        model.addAttribute("voting", votingService.getDetails(id));
        model.addAttribute("users", userService.listUsers());
        model.addAttribute("votingUrl", votingService.getVotingUrl(id));
        model.addAttribute("resultsUrl", votingService.getResultsUrl(id));
    }

    private void populateVotingPage(UUID id, Model model) {
        model.addAttribute("voting", votingService.getDetails(id));
        model.addAttribute("users", userService.listUsers());
    }

}
