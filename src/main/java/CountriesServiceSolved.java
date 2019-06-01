import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Function;

class CountriesServiceSolved implements CountriesService {

    @Override
    public Single<String> countryNameInCapitals(Country country) {
        return Single.just(country.getName().toUpperCase());
    }

    public Single<Integer> countCountries(List<Country> countries) {
        return Single.just(countries).map(List::size);
    }

    public Observable<Long> listPopulationOfEachCountry(List<Country> countries) {
        return Observable.fromArray(countries)
                .flatMapIterable(countryList -> countryList)
                .map(Country::getPopulation);
    }

    @Override
    public Observable<String> listNameOfEachCountry(List<Country> countries) {
        return Observable.fromArray(countries)
                .flatMapIterable(countryList -> countryList)
                .map(country -> country.getName());
    }

    @Override
    public Observable<Country> listOnly3rdAnd4thCountry(List<Country> countries) {
        return Observable.fromArray(countries)
                .flatMapIterable(countryList -> countryList)
                .skip(2)
                .take(2);
    }

    @Override
    public Single<Boolean> isAllCountriesPopulationMoreThanOneMillion(List<Country> countries) {
        return Single.just(countries.stream()
                .allMatch(x -> x.getPopulation() > 1000000));
    }

    @Override
    public Observable<Country> listPopulationMoreThanOneMillion(List<Country> countries) {
        return Observable.fromArray(countries)
                .flatMapIterable(countryList -> countryList)
                .filter(country -> country.getPopulation() > 1000000);
    }

    @Override
    public Observable<Country> listPopulationMoreThanOneMillionWithTimeoutFallbackToEmpty(final FutureTask<List<Country>> countriesFromNetwork) {
        return Observable.fromFuture(countriesFromNetwork, 1, TimeUnit.SECONDS)
                .flatMapIterable(countryList -> countryList)
                .onErrorResumeNext(Observable.empty())
                .filter(country -> country.getPopulation() > 1000000);
    }

    @Override
    public Observable<String> getCurrencyUsdIfNotFound(String countryName, List<Country> countries) {
        return Observable.fromArray(countries).flatMapIterable(countries1 -> countries1).filter(country -> country.getName().equals(countryName)).map(Country::getCurrency).first("USD").toObservable();
    }

    @Override
    public Observable<Long> sumPopulationOfCountries(List<Country> countries) {
        return Observable.fromArray(countries)
                .flatMapIterable(countries1 -> countries1)
                .map(country -> country.getPopulation())
                .reduce((country1, country2) -> country1 + country2).toObservable();
    }

    @Override
    public Single<Map<String, Long>> mapCountriesToNamePopulation(List<Country> countries) {
        return Observable.fromArray(countries).flatMapIterable(countries1 -> countries1).toMap(country -> country.getName(), country -> country.getPopulation());
    }

    @Override
    public Observable<Long> sumPopulationOfCountries(Observable<Country> countryObservable1,
                                                     Observable<Country> countryObservable2) {
        return countryObservable1.concatWith(countryObservable2)
                .map(Country::getPopulation)
                .reduce((x,y) -> x+y)
                .toObservable();
    }

    @Override
    public Single<Boolean> areEmittingSameSequences(Observable<Country> countryObservable1,
                                                    Observable<Country> countryObservable2) {
        return Observable.sequenceEqual(countryObservable1, countryObservable2);
    }
}
