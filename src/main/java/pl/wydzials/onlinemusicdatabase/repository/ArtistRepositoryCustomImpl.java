package pl.wydzials.onlinemusicdatabase.repository;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import pl.wydzials.onlinemusicdatabase.model.Artist;
import pl.wydzials.onlinemusicdatabase.model.Artist.ArtistType;
import pl.wydzials.onlinemusicdatabase.utils.Validation;

public class ArtistRepositoryCustomImpl implements ArtistRepositoryCustom {

  @PersistenceContext
  private EntityManager entityManager;

  public List<Artist> findTopArtists(final int maxResults, final ArtistType artistTypeFilter,
      final int minRatingsFilter, final int maxRatingsFilter) {
    Validation.inclusiveBetween(0, 100, maxResults);

    if (artistTypeFilter != null) {
      System.out.println(artistTypeFilter);
      return entityManager.createQuery("select a from Artist a "
              + "where a.artistType = :artistType "
              + "and a.numberOfRatings >= :minRatingFilter "
              + "and a.numberOfRatings <= :maxRatingFilter "
              + "order by a.averageRating desc", Artist.class)
          .setMaxResults(maxResults)
          .setParameter("artistType", artistTypeFilter)
          .setParameter("minRatingFilter", minRatingsFilter)
          .setParameter("maxRatingFilter", maxRatingsFilter)
          .getResultList();
    } else {
      return entityManager.createQuery("select a from Artist a "
              + "where a.numberOfRatings >= :minRatingFilter "
              + "and a.numberOfRatings <= :maxRatingFilter "
              + "order by a.averageRating desc", Artist.class)
          .setMaxResults(maxResults)
          .setParameter("minRatingFilter", minRatingsFilter)
          .setParameter("maxRatingFilter", maxRatingsFilter)
          .getResultList();
    }
  }
}
