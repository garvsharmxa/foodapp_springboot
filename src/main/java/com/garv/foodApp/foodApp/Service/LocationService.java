package com.garv.foodApp.foodApp.Service;

import com.garv.foodApp.foodApp.DTO.LocationDTO;
import com.garv.foodApp.foodApp.Entity.Location;
import com.garv.foodApp.foodApp.Repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationService {

    private final LocationRepository locationRepository;

    public List<LocationDTO> getAllLocations() {
        return locationRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public LocationDTO createLocation(LocationDTO locationDTO) {
        Location location = mapToEntity(locationDTO);
        Location saved = locationRepository.save(location);
        return mapToDTO(saved);
    }

    public LocationDTO getLocationById(UUID id) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + id));
        return mapToDTO(location);
    }

    public LocationDTO updateLocation(UUID id, LocationDTO locationDTO) {
        Location location = locationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Location not found with id: " + id));
        location.setName(locationDTO.getName());
        location.setAddress(locationDTO.getAddress());
        Location updated = locationRepository.save(location);
        return mapToDTO(updated);
    }

    public void deleteLocation(UUID id) {
        if (!locationRepository.existsById(id)) {
            throw new RuntimeException("Location not found with id: " + id);
        }
        locationRepository.deleteById(id);
    }

    private LocationDTO mapToDTO(Location location) {
        return LocationDTO.builder()
                .id(location.getId())
                .name(location.getName())
                .address(location.getAddress())
                .build();
    }

    private Location mapToEntity(LocationDTO dto) {
        Location location = new Location();
        location.setName(dto.getName());
        location.setAddress(dto.getAddress());
        return location;
    }
}
