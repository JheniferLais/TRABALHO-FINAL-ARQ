package com.sched.api.service;

import com.sched.api.dto.request.UserUpdateRequest;
import com.sched.api.dto.response.UserResponse;
import com.sched.api.exception.AccessDeniedException;
import com.sched.api.exception.ResourceNotFoundException;
import com.sched.api.domain.User;
import com.sched.api.repository.UserRepository;
import com.sched.api.security.AuthenticatedUserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Transactional(readOnly = true)
    public UserResponse me() {
        User authUser = authenticatedUserProvider.getCurrentUser();

        User user = userRepository.findByEmail(authUser.getEmail())
                .orElseThrow(ResourceNotFoundException::new);

        return mapToResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAll() {
        User authUser = authenticatedUserProvider.getCurrentUser();
        return userRepository.findAllByCompanyIdAndDeletedFalse(authUser.getCompany().getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponse getById(Long id) {
        User authUser = authenticatedUserProvider.getCurrentUser();

        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(ResourceNotFoundException::new);

        validateCompanyAccess(authUser, user);

        return mapToResponse(user);
    }

    @Transactional
    public UserResponse update(Long id, UserUpdateRequest dto) {
        User authUser = authenticatedUserProvider.getCurrentUser();

        if (!authUser.getId().equals(id) && !isAdmin(authUser)) {
            throw new AccessDeniedException();
        }

        User userToUpdate = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(ResourceNotFoundException::new);

        validateCompanyAccess(authUser, userToUpdate);

        userToUpdate.setName(dto.name());
        userToUpdate.setEmail(dto.email());

        return mapToResponse(userRepository.save(userToUpdate));
    }

    @Transactional
    public void delete(Long id) {
        User authUser = authenticatedUserProvider.getCurrentUser();

        User user = userRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(ResourceNotFoundException::new);

        validateCompanyAccess(authUser, user);

        user.setDeleted(true);

        userRepository.save(user);
    }

    private void validateCompanyAccess(User authUser, User targetUser) {
        if (!authUser.getCompany().getId().equals(targetUser.getCompany().getId())) {
            throw new AccessDeniedException();
        }
    }

    private boolean isAdmin(User user) {
        return user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    private UserResponse mapToResponse(User user) {
        return new UserResponse(user);
    }
}
