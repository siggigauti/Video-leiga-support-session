package is.hi.hbv501.videoleiga.videoleiga.Controllers;

import is.hi.hbv501.videoleiga.videoleiga.Entities.Genre;
import is.hi.hbv501.videoleiga.videoleiga.Entities.Movie;
import is.hi.hbv501.videoleiga.videoleiga.Entities.RentalLog;
import is.hi.hbv501.videoleiga.videoleiga.Entities.User;
import is.hi.hbv501.videoleiga.videoleiga.Services.MovieService;
import is.hi.hbv501.videoleiga.videoleiga.Services.RentalLogService;
import is.hi.hbv501.videoleiga.videoleiga.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Controller
public class HomeController {

    private MovieService movieService;
    private RentalLogService rentalLogService;
    private UserService userService;

    @Autowired
    public HomeController(MovieService movieService, RentalLogService rentalLogService, UserService userService){
        this.rentalLogService = rentalLogService;
        this.userService = userService;
        this.movieService = movieService;
    }

    @RequestMapping("/")
    public String Home(Model model){
        model.addAttribute("movies", movieService.findAll());
        return "Velkominn";
    }

    @RequestMapping(value ="/addmovie", method = RequestMethod.POST)
    public String addMovie(@Valid Movie movie, BindingResult result, Model model){
        if(result.hasErrors()){
            return "add-movie";
        }
        movieService.save(movie);
        model.addAttribute("movies", movieService.findAll());
        return "Velkominn";
    }

    @RequestMapping(value="/addmovie", method = RequestMethod.GET)
    public String addMovieForm(Movie movie){
        return "add-movie";
    }

    @RequestMapping(value="/delete/{id}", method = RequestMethod.GET)
    public String deleteMovie(@PathVariable("id") long id, Model model){
        Movie movie = movieService.findById(id).orElseThrow(()-> new IllegalArgumentException("Invalid movie ID"));
        movieService.delete(movie);
        model.addAttribute("movies", movieService.findAll());
        return "Velkominn";
    }

    @RequestMapping("/makedata")
    public String makeData(Model model){
        HashSet<Genre> genres = new HashSet<>();
        genres.add(Genre.ADVENTURE);
        genres.add(Genre.ACTION);
        for (int i = 0; i < 3; i++) {
            this.movieService.save(new Movie("Great movie "+i,"fantastic movie in a trilogy",Double.valueOf(i),genres));
        }
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        User tempUser = new User("Karl Jóhann","pass123");
        List<Movie> tempMovie = movieService.findAll();
        this.userService.save(tempUser);
        try {
            rentalLogService.save(new RentalLog(tempMovie.get(0),tempUser,sdf.parse("21/12/2012"),sdf.parse("31/12/2013") ));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        model.addAttribute("movies", movieService.findAll());
        return "Velkominn";
    }

    @RequestMapping("/rentals")
    public String allRentals(Model model){
        model.addAttribute("rentalLog", rentalLogService.findAll());
        return "rentals";
    }

    @RequestMapping("/search")
    public String search(){
        return "search";
    }

    @RequestMapping(value= "/movieSearch", method = RequestMethod.POST)
    public String searchMovie(@RequestParam(value = "search", required = false) String search, Model model){

        List<Movie> movie = movieService.findByTitle(search);
        model.addAttribute("movies", movie);
        return "Velkominn";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String signUpGET(User user){
        return "signup";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String signUpPOST(@Valid User user, BindingResult result, Model model){
        if(result.hasErrors()){
            return "signup";
        }
        User exists = userService.findByUName(user.uName);
        if(exists == null){
            userService.save(user);
        }
        model.addAttribute("movies", movieService.findAll());
        return "Velkominn";
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String usersGET(Model model){
        model.addAttribute("users", userService.findAll());
        return "users";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginGET(User user){
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginPOST(@Valid User user, BindingResult result, Model model, HttpSession session){
        if(result.hasErrors()){
            return "login";
        }
        model.addAttribute("movies",movieService.findAll());
        User exists = userService.login(user);
        if(exists != null){
            session.setAttribute("LoggedInUser", user);
            return "redirect:/";
        }
        return "redirect:/";
    }

    @RequestMapping(value = "/loggedin", method = RequestMethod.GET)
    public String loggedinGET(HttpSession session, Model model){
        model.addAttribute("movies",movieService.findAll());
        User sessionUser = (User) session.getAttribute("LoggedInUser");
        if(sessionUser  != null){
            model.addAttribute("loggedinuser", sessionUser);
            return "loggedInUser";
        }
        return "redirect:/";
    }
}
