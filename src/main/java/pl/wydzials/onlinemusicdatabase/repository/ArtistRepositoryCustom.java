package pl.wydzials.onlinemusicdatabase.repository;

import java.util.List;
import pl.wydzials.onlinemusicdatabase.model.Artist;

public interface ArtistRepositoryCustom {

  List<Artist> findTopArtists(final int maxResults);
}
