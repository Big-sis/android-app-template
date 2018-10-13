package fr.wildcodeschool.vyfe.repository;

public interface IMapper<From, To> {

    To map(From from);
}
