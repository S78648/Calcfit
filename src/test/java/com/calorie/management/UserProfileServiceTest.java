package com.calorie.management;

import com.calorie.management.dto.*;
import com.calorie.management.entity.User;
import com.calorie.management.entity.UserProfile;
import com.calorie.management.entity.UserTarget;
import com.calorie.management.enums.ActivityLevel;
import com.calorie.management.enums.Gender;
import com.calorie.management.enums.GoalType;
import com.calorie.management.exception.ResourceNotFoundException;
import com.calorie.management.repository.UserProfileRepository;
import com.calorie.management.repository.UserRepository;
import com.calorie.management.repository.UserTargetRepository;
import com.calorie.management.service.TargetCalculationService;
import com.calorie.management.service.UserProfileService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserProfileServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @Mock
    private UserTargetRepository userTargetRepository;

    @Mock
    private TargetCalculationService targetCalculationService;

    @InjectMocks
    private UserProfileService userProfileService;

    @Test
    void shouldCreateNewProfile() {

        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .email("test@gmail.com")
                .build();

        UserProfileRequest request =
                new UserProfileRequest(
                        "Sumit",
                        26,
                        Gender.MALE,
                        BigDecimal.valueOf(168),
                        BigDecimal.valueOf(65),
                        ActivityLevel.MODERATE,
                        GoalType.WEIGHT_GAIN,
                        null,
                        null
                );

//        when(userProfileRepository.findByUserId(userId))
//                .thenReturn(Optional.empty());

        UserProfile savedProfile = UserProfile.builder().age(20).gender(Gender.MALE).user(user).build();

        when(userProfileRepository.findByUserId(userId))
                .thenReturn(Optional.empty())          // first call in saveProfile()
                .thenReturn(Optional.of(savedProfile)); // second call in buildDashboardResponse()

        when(userProfileRepository.save(any(UserProfile.class)))
                .thenReturn(savedProfile);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

//        when(userProfileRepository.save(any(UserProfile.class)))
//                .thenAnswer(invocation -> (UserProfile) invocation.getArgument(0));

        userProfileService.saveProfile(userId, request);

        verify(userRepository, times(2))
                .findById(userId);

        verify(userProfileRepository)
                .save(any(UserProfile.class));
    }

    @Test
    void shouldUpdateExistingProfile() {

        UUID userId = UUID.randomUUID();

        User user = User.builder()
                .id(userId)
                .email("test@gmail.com")
                .build();

        UserProfileRequest request =
                new UserProfileRequest(
                        "Sumit",
                        26,
                        Gender.MALE,
                        BigDecimal.valueOf(168),
                        BigDecimal.valueOf(65),
                        ActivityLevel.MODERATE,
                        GoalType.WEIGHT_GAIN,
                        null,
                        null
                );

        UserProfile existingProfile =
                UserProfile.builder()
                        .userId(userId)
                        .build();

        when(userProfileRepository.findByUserId(userId))
                .thenReturn(Optional.of(existingProfile));

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(userProfileRepository.save(any(UserProfile.class)))
                .thenReturn(existingProfile);

        userProfileService.updateProfile(userId, request);

        verify(userProfileRepository)
                .save(any(UserProfile.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingMissingProfile() {

        UUID userId = UUID.randomUUID();

        when(userProfileRepository.findByUserId(userId))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> userProfileService.updateProfile(
                        userId,
                        mock(UserProfileRequest.class)
                )
        );
    }

    @Test
    void shouldReturnDashboard() {

        UUID userId = UUID.randomUUID();

        User user =
                User.builder()
                        .id(userId)
                        .email("test@gmail.com")
                        .build();

        UserProfile profile =
                UserProfile.builder()
                        .user(user)
                        .fullName("Sumit")
                        .age(26)
                        .gender(Gender.MALE)
                        .build();

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        when(userProfileRepository.findByUserId(userId))
                .thenReturn(Optional.of(profile));

        UserDashboardResponse response =
                userProfileService.getProfile(userId);

        assertNotNull(response);
    }

    @Test
    void lossTargetMustBeBelowCurrentWeight() {
        UUID userId = UUID.randomUUID();
        UserProfile existing = UserProfile.builder().userId(userId).gender(Gender.MALE)
                .weightKg(BigDecimal.valueOf(80)).build();
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(existing));

        UserProfileRequest request = new UserProfileRequest(null, null, null, null, null, null,
                GoalType.WEIGHT_LOSS, BigDecimal.valueOf(85), BigDecimal.valueOf(0.5));

        com.calorie.management.exception.InvalidRequestException ex = org.junit.jupiter.api.Assertions.assertThrows(
                com.calorie.management.exception.InvalidRequestException.class,
                () -> userProfileService.updateProfile(userId, request));
        org.junit.jupiter.api.Assertions.assertTrue(ex.getMessage().startsWith("targetWeightKg: must be below"));
        verify(userProfileRepository, never()).save(any(UserProfile.class));
    }

    @Test
    void gainTargetMustBeAboveCurrentWeight() {
        UUID userId = UUID.randomUUID();
        UserProfile existing = UserProfile.builder().userId(userId).gender(Gender.MALE)
                .weightKg(BigDecimal.valueOf(60)).build();
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(existing));

        UserProfileRequest request = new UserProfileRequest(null, null, null, null, null, null,
                GoalType.WEIGHT_GAIN, BigDecimal.valueOf(60), BigDecimal.valueOf(0.25));

        org.junit.jupiter.api.Assertions.assertThrows(
                com.calorie.management.exception.InvalidRequestException.class,
                () -> userProfileService.updateProfile(userId, request));
    }

    @Test
    void maintenanceClearsTargetAndPace() {
        UUID userId = UUID.randomUUID();
        User user = User.builder().id(userId).email("m@calcfit.dev").build();
        UserProfile existing = UserProfile.builder().userId(userId).user(user).gender(Gender.FEMALE)
                .weightKg(BigDecimal.valueOf(60)).goalType(GoalType.WEIGHT_LOSS)
                .targetWeightKg(BigDecimal.valueOf(55)).weeklyPaceKg(BigDecimal.valueOf(0.5)).build();
        when(userProfileRepository.findByUserId(userId)).thenReturn(Optional.of(existing));
        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(i -> i.getArgument(0));
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userProfileService.updateProfile(userId, new UserProfileRequest(null, null, null, null, null, null,
                GoalType.MAINTENANCE, null, null));

        org.junit.jupiter.api.Assertions.assertNull(existing.getTargetWeightKg());
        org.junit.jupiter.api.Assertions.assertNull(existing.getWeeklyPaceKg());
    }
}
