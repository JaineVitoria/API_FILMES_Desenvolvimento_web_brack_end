package com.ifsp.apifilmes.service;

import com.ifsp.apifilmes.dto.FilmeRequestDTO;
import com.ifsp.apifilmes.exeption.EntidadeNaoEncontrdaException;
import com.ifsp.apifilmes.model.Ator;
import com.ifsp.apifilmes.model.Diretor;
import com.ifsp.apifilmes.model.Filme;
import com.ifsp.apifilmes.model.Genero;
import com.ifsp.apifilmes.repository.FilmeRepository;
import com.ifsp.apifilmes.websocket.FilmeWebSocketHandler;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class FilmeService {
    private final FilmeRepository filmeRepository;
    private final GeneroService generoService;
    private final DiretorService diretorService;
    private final AtorService atorService;
    private final FilmeWebSocketHandler webSocketHandler;

    public FilmeService(FilmeRepository filmeRepository, GeneroService generoService,
                        DiretorService diretorService, AtorService atorService,
                        FilmeWebSocketHandler webSocketHandler) {
        this.filmeRepository = filmeRepository;
        this.generoService = generoService;
        this.diretorService = diretorService;
        this.atorService = atorService;
        this.webSocketHandler = webSocketHandler;
    }

    private Filme montarFilme(FilmeRequestDTO filmeRequestDTO) {
        Filme filme = new Filme();

        filme.setTitulo(filmeRequestDTO.titulo());
        filme.setSnopse(filmeRequestDTO.sinopse());
        filme.setDataLancamento(filmeRequestDTO.dataLancamento());

        Genero genero = generoService.listarGeneroPorId(filmeRequestDTO.generoId());
        filme.setGenero(genero);

        Diretor diretor = diretorService.listarDiretorPorId(filmeRequestDTO.diretorId());
        filme.setDiretor(diretor);

        List<Ator> atores = atorService.listarAtoresPorIds(filmeRequestDTO.atoresIds());
        filme.setAtores(atores);

        return filme;
    }

    public Filme salvarFilme(FilmeRequestDTO filmeRequestDTO) {
        Filme salvo = filmeRepository.save(montarFilme(filmeRequestDTO));
        webSocketHandler.notificar("FILME_CADASTRADO", salvo.getId(), salvo.getTitulo());
        return salvo;
    }

    public List<Filme> listarFilmes() {
        return filmeRepository.findAll();
    }

    public Filme listarFilmePorId(Integer id) {
        Optional<Filme> filme = filmeRepository.findById(id);
        if (filme.isEmpty()) {
            throw new EntidadeNaoEncontrdaException("O filme de id " + id + " nao foi encontrado na base.", "Recurso nao encontrado");
        }
        return filme.get();
    }

    public Filme atualizarFilme(FilmeRequestDTO filmeRequestDTO, Integer id) {
        Filme filme = listarFilmePorId(id);

        filme.setTitulo(filmeRequestDTO.titulo());
        filme.setSnopse(filmeRequestDTO.sinopse());
        filme.setDataLancamento(filmeRequestDTO.dataLancamento());
        filme.setGenero(generoService.listarGeneroPorId(filmeRequestDTO.generoId()));
        filme.setDiretor(diretorService.listarDiretorPorId(filmeRequestDTO.diretorId()));
        filme.setAtores(atorService.listarAtoresPorIds(filmeRequestDTO.atoresIds()));

        Filme atualizado = filmeRepository.save(filme);
        webSocketHandler.notificar("FILME_ATUALIZADO", atualizado.getId(), atualizado.getTitulo());
        return atualizado;
    }

    public void deletarFilme(Integer id) {
        Filme filme = listarFilmePorId(id);
        Integer idRemovido = filme.getId();
        String titulo = filme.getTitulo();
        filmeRepository.deleteById(idRemovido);
        webSocketHandler.notificar("FILME_REMOVIDO", idRemovido, titulo);
    }
}