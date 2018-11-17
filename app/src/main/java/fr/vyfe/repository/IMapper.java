package fr.vyfe.repository;


import java.util.HashMap;
import java.util.List;

public interface IMapper<From, To> {

    To map(From from, String key);

    List<To> mapList(HashMap<String, From> from);
}
