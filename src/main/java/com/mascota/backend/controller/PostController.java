package com.mascota.backend.controller;

import com.mascota.backend.model.Post;
import com.mascota.backend.model.User;
import com.mascota.backend.repository.PostRepository;
import com.mascota.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping
public class PostController {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/home/{id}")
    public String home(Model model, @PathVariable("id") int id) {
        List<Post> posts = postRepository.findAll();
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + id));
        model.addAttribute("posts", posts);
        model.addAttribute("user", user);
        return "home";
    }

    @GetMapping("/post/new/{id}")
    public String addPost(@PathVariable(value = "id") Integer id, Model model) {
        User user = userRepository.getReferenceById(id);
        model.addAttribute("post", new Post());
        model.addAttribute("user", user);
        return "add";
    }

    @PostMapping("/newPost/{id}")
    public String savePost(@PathVariable(value = "id") Integer id, @ModelAttribute Post postRequest, RedirectAttributes redirectAttributes, Model model) {
        Post post = userRepository.findById(id).map(user -> {
            postRequest.setUser(user);
            model.addAttribute("user", user);
            return postRepository.save(postRequest);
        }).orElseThrow();
        redirectAttributes.addFlashAttribute("user", post.getUser());
        return "redirect:/home/" + post.getUser().getId();
    }

    @GetMapping("/post/delete/{id}")
    public String deletePost(@ModelAttribute @PathVariable("id") int id) {
        try {
            Post post = postRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid post Id:" + id));
            postRepository.deleteById(post.getId());
            return ("redirect:/user/" + post.getUser().getId());
        } catch (Exception e) {
            return ("error");
        }
    }

    @GetMapping("/post/edit/{id}")
    public String edit(Model model, @PathVariable("id") int id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid post id:" + id));
        model.addAttribute("post", post);
        model.addAttribute("user", post.getUser());
        return "updatePost";
    }

    @PostMapping("/post/update/{id}")
    public String update(@ModelAttribute Post post, @PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        List<Post> posts = postRepository.findAll();
        Post postList = null;
        for (int i = 0; i < posts.size(); i++) {
            postList = posts.get(i);
            if (postList.getId() == post.getId()) {
                postList.setDescr(post.getDescr());
                postList.setReward(post.getReward());
                postRepository.save(postList);
            }
        }
        return "redirect:/user/" + postList.getUser().getId();
    }

}
