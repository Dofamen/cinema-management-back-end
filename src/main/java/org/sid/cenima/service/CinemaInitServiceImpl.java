package org.sid.cenima.service;

import org.sid.cenima.dao.*;
import org.sid.cenima.entities.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

@Service // Metier
@Transactional
public class CinemaInitServiceImpl implements ICinemaInitService{

    @Autowired
    private VilleRepository villeRepository;
    @Autowired
    private CinemaRepository cinemaRepository;
    @Autowired
    private SalleRepository salleRepository;
    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private SeanceRepository seanceRepository;
    @Autowired
    private FilmRepository filmRepository;
    @Autowired
    private ProjectionRepository projectionRepository;
    @Autowired
    private TicketRepository ticketRepository;
    @Autowired
    private CategorieRepository categorieRepository;

    @Override
    public void initVilles() {
        Stream.of("Casablanca", "Marrakech", "Rabat", "Tanger").forEach(name -> {
            Ville ville = new Ville();
            ville.setName(name);
            villeRepository.save(ville);
        });
    }

    @Override
    public void initCinemas() {

        villeRepository.findAll().forEach(v -> {
                Stream.of("Megarama", "IMAX", "Founoun", "Chahrazad", "Daouliz")
                        .forEach(name -> {
                            Cinema cinema = new Cinema();
                            cinema.setName(name);
                            cinema.setNombreSalles((int)(Math.random()*7+3));
                            cinema.setVille(v);
                            cinemaRepository.save(cinema);
                        });
        });
    }

    @Override
    public void initSalles() {
            cinemaRepository.findAll().forEach(cinema -> {
                for (int i=0; i<cinema.getNombreSalles(); i++){
                    Salle salle = new Salle();
                    salle.setName("Salle" + (i+1));
                    salle.setCinema(cinema);
                    salle.setNombrePlaces(20+(int)(Math.random()*15));
                    salleRepository.save(salle);
                }
            });
    }

    @Override
    public void initPlaces() {
            salleRepository.findAll().forEach(salle -> {
                for (int i=0; i<salle.getNombrePlaces(); i++){
                    Place place = new Place();
                    place.setNumero(i+1);
                    place.setSalle(salle);
                    placeRepository.save(place);
                }
            });
    }

    @Override
    public void initSeances() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        Stream.of("12:00", "15:00", "17:00", "19:00", "21:00").forEach(s -> {
            Seance seance = new Seance();
            try {
                seance.setHoureDebut(dateFormat.parse(s));
                seanceRepository.save(seance);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void initCategories() {
        Stream.of("Action", "Histoire", "Darama", "Fiction").forEach(s ->
        {
            Categorie categorie = new Categorie();
            categorie.setName(s);
            categorieRepository.save(categorie);
        });
    }

    @Override
    public void initFilms() {
        List<Categorie> categories = categorieRepository.findAll();
        Stream.of("Greyhound", "The king", "Spider-man", "Interstellar", "Iron-Man").forEach(movie ->{
            Film film = new Film();
            film.setTitle(movie);
            film.setDuree((int)(Math.random()*90)+30);
            film.setImage(movie.replaceAll(" ", "")+".jpg");
            film.setCategorie(categories.get(new Random().nextInt(categories.size())));
            filmRepository.save(film);
        });
    }

    @Override
    public void initProjections() {
        double[] prices = new double[]{30, 50, 70, 90, 100};
        List<Film> films = filmRepository.findAll();
        villeRepository.findAll().forEach(ville -> {
            ville.getCinemas().forEach(cinema -> {
                cinema.getSalles().forEach(salle -> {
                    int index = new Random().nextInt(films.size());
                    Film film = films.get(index);
                    seanceRepository.findAll().forEach(seance -> {
                            Projection projection = new Projection();
                            projection.setDateProjection(new Date());
                            projection.setFilm(film);
                            projection.setPrix(prices[new Random().nextInt(prices.length)]);
                            projection.setSalle(salle);
                            projection.setSeance(seance);
                            projectionRepository.save(projection);
                        });
                });
            });
        });
    }

    @Override
    public void initTickets() {

        projectionRepository.findAll().forEach(projection -> {
            projection.getSalle().getPlaces().forEach(place -> {
                System.out.println("set");
                Ticket ticket = new Ticket();
                ticket.setPlace(place);
                ticket.setPrix(projection.getPrix());
                ticket.setProjection(projection);
                ticket.setReserve(false);
                ticketRepository.save(ticket);
            });
        });
    }
}
