package pl.wydzials.onlinemusicdatabase.repository;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import pl.wydzials.onlinemusicdatabase.model.Artist;
import pl.wydzials.onlinemusicdatabase.utils.Validation;

public class ArtistRepositoryCustomImpl implements ArtistRepositoryCustom {

  @PersistenceContext
  private EntityManager entityManager;

  public List<Artist> findTopArtists(final int maxResults) {
    Validation.inclusiveBetween(0, 100, maxResults);

    return entityManager.createQuery("select a from Artist a order by a.averageRating desc", Artist.class)
        .setMaxResults(maxResults)
        .getResultList();
  }
}
