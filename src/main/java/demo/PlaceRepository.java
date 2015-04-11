package demo;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PlaceRepository extends MongoRepository<Place, String> {

	
	List<Place> findByCity(String s);
	
	
    List<Place> findByPositionNear(org.springframework.data.geo.Point p,
                                   org.springframework.data.geo.Distance d);
}