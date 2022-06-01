package it.polito.tdp.yelp.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import it.polito.tdp.yelp.db.YelpDao;

public class Model {
	
	private Graph<User, DefaultWeightedEdge> graph;
	List<User> utenti;
	
	public String creaGrafo(int minRevisioni, int anno) {
		this.graph = new SimpleWeightedGraph<User, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		YelpDao dao = new YelpDao();
		this.utenti = dao.getUsersWithReviews(minRevisioni);
		Graphs.addAllVertices(this.graph, this.utenti);
		for(User u1 : this.utenti) {
			for(User u2 : this.utenti) {
				if(!u1.equals(u2) && u1.getUserId().compareTo(u2.getUserId()) < 0) {
					int sim = dao.calcolaSimilarita(u1, u2, anno);
					if(sim > 0) {
						Graphs.addEdge(this.graph, u1, u2, sim);
					}
				}
			}
		}
		return "Grafo creato con " +this.graph.vertexSet().size() + " vertici e " + this.graph.edgeSet().size() +" archi";
	}
	
	public List<User> utentiPiuSimili(User u){
		
		int max = 0;
		for(DefaultWeightedEdge e : this.graph.edgesOf(u)) {
			if(this.graph.getEdgeWeight(e) > max) {
				max = (int) this.graph.getEdgeWeight(e);
			}
		}
		
		List<User> res = new ArrayList<>();
		for(DefaultWeightedEdge e : this.graph.edgesOf(u)) {
			if((int)this.graph.getEdgeWeight(e) == max) {
				User u2 = Graphs.getOppositeVertex(this.graph,e, u);
				res.add(u2);
			}
		}
		return res;
	}
	
	
	public List<User> getUsers(){
		return this.utenti;
	}
}
