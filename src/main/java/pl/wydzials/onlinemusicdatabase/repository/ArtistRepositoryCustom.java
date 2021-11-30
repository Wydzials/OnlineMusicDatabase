package pl.wydzials.onlinemusicdatabase.repository;

import java.util.List;
import pl.wydzials.onlinemusicdatabase.model.Artist;
import pl.wydzials.onlinemusicdatabase.model.Artist.ArtistType;

public interface ArtistRepositoryCustom {

  List<Artist> findTopArtists(final int maxResults, final ArtistType artistTypesFilter,
      final int minRatingsFilter, final int maxRatingsFilter);
}
