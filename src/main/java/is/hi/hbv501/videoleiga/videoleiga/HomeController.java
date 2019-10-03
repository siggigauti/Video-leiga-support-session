package is.hi.hbv501.videoleiga.videoleiga;

import is.hi.hbv501.videoleiga.videoleiga.Entities.Movie;
import is.hi.hbv501.videoleiga.videoleiga.Services.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.validation.Valid;

@Controller
public class HomeController {

    private MovieService movieService;
    @Autowired
    public HomeController(MovieService movieService){this.movieService = movieService;}

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
    public String addMovieForm(Model model){
        return "add-movie";
    }

    @RequestMapping(value="/delete/{id}", method = RequestMethod.GET)
    public String deleteMovie(@PathVariable("id") long id, Model model){
        Movie movie = movieService.findById(id).orElseThrow(()-> new IllegalArgumentException("Invalid movie ID"));
        movieService.delete(movie);
        model.addAttribute("movies", movieService.findAll());
        return "Velkominn";
    }
}
