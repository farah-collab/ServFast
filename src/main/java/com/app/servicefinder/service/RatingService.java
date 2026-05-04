package com.app.servicefinder.service;
 
import com.app.servicefinder.dto.rating.*;
import com.app.servicefinder.model.*;
import com.app.servicefinder.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
 
@Service
@RequiredArgsConstructor
public class RatingService {
 
    private final RatingRepository ratingRepository;
    private final ServiceRepository serviceRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final ServiceService serviceService;
 
    public RatingResponseDTO addRating(Long reviewerId, RatingRequest request) {
        if (ratingRepository.existsByReviewerIdAndServiceId(reviewerId, request.getServiceId())) {
            throw new RuntimeException("Vous avez déjà noté ce service");
        }
        User reviewer = userRepository.findById(reviewerId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        com.app.servicefinder.model.Service service = serviceRepository.findById(request.getServiceId())
                .orElseThrow(() -> new RuntimeException("Service non trouvé"));
 
        Rating rating = Rating.builder()
                .score(request.getScore())
                .comment(request.getComment())
                .reviewer(reviewer)
                .service(service)
                .build();
        rating = ratingRepository.save(rating);
 
        // Notifier le propriétaire du service
        notificationService.createNotification(
            service.getUser().getId(),
            reviewer.getFullName() + " a laissé une note " + request.getScore() + "/5 sur votre service '" + service.getTitle() + "'",
            "RATING"
        );
 
        // Vérifier si le service doit être désactivé
        serviceService.checkAndDisableIfBadRatings(service.getId());
 
        return mapToDTO(rating);
    }
 
    public List<RatingResponseDTO> getByService(Long serviceId) {
        return ratingRepository.findByServiceId(serviceId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }
 
    private RatingResponseDTO mapToDTO(Rating r) {
        return RatingResponseDTO.builder()
                .id(r.getId())
                .score(r.getScore())
                .comment(r.getComment())
                .reviewerName(r.getReviewer().getFullName())
                .reviewerPhoto(r.getReviewer().getProfilePhoto())
                .createdAt(r.getCreatedAt())
                .build();
    }
}