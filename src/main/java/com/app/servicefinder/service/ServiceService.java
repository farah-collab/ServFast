package com.app.servicefinder.service;
 
import com.app.servicefinder.dto.service.*;
import com.app.servicefinder.dto.user.UserResponseDTO;
import com.app.servicefinder.model.*;
import com.app.servicefinder.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;
 
@Service
@RequiredArgsConstructor
public class ServiceService {
 
    private final ServiceRepository serviceRepository;
    private final CategoryRepository categoryRepository;
    private final ServicePhotoRepository photoRepository;
    private final UserRepository userRepository;
    private final RatingRepository ratingRepository;
    private final NotificationService notificationService;
 
    // Seuil : si > 60% de mauvaises notes (<=2), désactiver le service
    private static final double BAD_RATING_THRESHOLD = 0.6;
    private static final long MIN_RATINGS_FOR_CHECK = 5;
 
    public ServiceResponseDTO create(Long userId, ServiceCreateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
 
        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId()).orElse(null);
        }
 
        com.app.servicefinder.model.Service service = com.app.servicefinder.model.Service.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .price(request.getPrice())
                .priceType(request.getPriceType())
                .city(request.getCity())
                .latitude(request.getLatitude())
                .longitude(request.getLongitude())
                .user(user)
                .category(category)
                .isAvailable(true)
                .viewsCount(0)
                .build();
 
        service = serviceRepository.save(service);
 
        // Ajouter photos
        if (request.getPhotoUrls() != null) {
            final com.app.servicefinder.model.Service finalService = service;
            request.getPhotoUrls().forEach(url -> {
                photoRepository.save(ServicePhoto.builder()
                        .photoUrl(url)
                        .service(finalService)
                        .build());
            });
        }
        return mapToDTO(service);
    }
 
    public ServiceResponseDTO getById(Long serviceId) {
        com.app.servicefinder.model.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service non trouvé"));
        // Incrémenter les vues
        service.setViewsCount(service.getViewsCount() + 1);
        serviceRepository.save(service);
        return mapToDTO(service);
    }
 
    public List<ServiceResponseDTO> search(String keyword, String city, Long categoryId, Double maxPrice) {
        return serviceRepository.searchServices(keyword, city, categoryId, maxPrice)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }
 
    public List<ServiceResponseDTO> getByUser(Long userId) {
        return serviceRepository.findByUserId(userId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }
 
    public List<ServiceResponseDTO> getByCategory(Long categoryId) {
        return serviceRepository.findByCategoryId(categoryId)
                .stream().map(this::mapToDTO).collect(Collectors.toList());
    }
 
    public void delete(Long serviceId, Long userId) {
        com.app.servicefinder.model.Service service = serviceRepository.findById(serviceId)
                .orElseThrow(() -> new RuntimeException("Service non trouvé"));
        if (!service.getUser().getId().equals(userId)) {
            throw new RuntimeException("Non autorisé");
        }
        serviceRepository.delete(service);
    }
 
    @Transactional
    public void checkAndDisableIfBadRatings(Long serviceId) {
        Long total = ratingRepository.countByServiceId(serviceId);
        if (total < MIN_RATINGS_FOR_CHECK) return;
 
        Long badCount = serviceRepository.countBadRatings(serviceId);
        double ratio = (double) badCount / total;
 
        if (ratio >= BAD_RATING_THRESHOLD) {
            com.app.servicefinder.model.Service service = serviceRepository.findById(serviceId)
                    .orElseThrow(() -> new RuntimeException("Service non trouvé"));
            service.setIsAvailable(false);
            serviceRepository.save(service);
            // Notifier le propriétaire
            notificationService.createNotification(
                service.getUser().getId(),
                "Votre service '" + service.getTitle() + "' a été suspendu automatiquement en raison de trop mauvaises évaluations.",
                "REPORT"
            );
        }
    }
 
    public ServiceResponseDTO mapToDTO(com.app.servicefinder.model.Service s) {
        List<String> photos = photoRepository.findByServiceId(s.getId())
                .stream().map(ServicePhoto::getPhotoUrl).collect(Collectors.toList());
 
        Double avg = serviceRepository.getAverageRating(s.getId());
        Long total = ratingRepository.countByServiceId(s.getId());
 
        return ServiceResponseDTO.builder()
                .id(s.getId())
                .title(s.getTitle())
                .description(s.getDescription())
                .price(s.getPrice())
                .priceType(s.getPriceType())
                .city(s.getCity())
                .latitude(s.getLatitude())
                .longitude(s.getLongitude())
                .isAvailable(s.getIsAvailable())
                .viewsCount(s.getViewsCount())
                .categoryName(s.getCategory() != null ? s.getCategory().getName() : null)
                .categoryIcon(s.getCategory() != null ? s.getCategory().getIconUrl() : null)
                .provider(UserResponseDTO.builder()
                        .id(s.getUser().getId())
                        .fullName(s.getUser().getFullName())
                        .profilePhoto(s.getUser().getProfilePhoto())
                        .city(s.getUser().getCity())
                        .build())
                .averageRating(avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0)
                .totalRatings(total)
                .photoUrls(photos)
                .createdAt(s.getCreatedAt())
                .build();
    }
}