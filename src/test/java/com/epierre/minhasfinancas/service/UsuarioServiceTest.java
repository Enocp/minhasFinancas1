package com.epierre.minhasfinancas.service;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.epierre.minhasfinancas.exception.ErroAutenticacao;
import com.epierre.minhasfinancas.exception.RegraNegocioException;
//import com.epierre.minhasfinancas.exception.RegraNegocioException;
import com.epierre.minhasfinancas.model.entity.Usuario;
import com.epierre.minhasfinancas.model.repository.UsuarioRepository;
import com.epierre.minhasfinancas.service.impl.UsuarioServiceImpl;

@SpringBootTest
@RunWith(SpringRunner.class)
//@ActiveProfiles("test")
public class UsuarioServiceTest {
	
	@SpyBean
	UsuarioServiceImpl service;
	//@Autowired
	//UsuarioService service;
	
	//@Autowired
	@MockBean
	UsuarioRepository repository;
	
	//@Before
	//public void setup() {
		// usar do spy
		//service = Mockito.spy(UsuarioServiceImpl.class);
		//repository = Mockito.mock(UsuarioRepository.class);
		//service = new UsuarioServiceImpl(repository);
	//}
	@Test(expected = Test.None.class)
	public void deveSalvarUmUsuario() {
		//cenario
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
		Usuario usuario = Usuario.builder()
				.id(11)
				.nome("nome")
				.email("email@email.com")
				.senha("senha").build();
		Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
		//acao
		Usuario usuarioSalvo = service.salvarUsuario(new Usuario());
		//verificar
		Assertions.assertThat(usuarioSalvo).isNotNull();
		Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(11);
		Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("nome");
		Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("email@email.com");
		Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");

	}
	
	@Test(expected = RegraNegocioException.class)
	public void naoDeveSalvarUnUsuarioComEmailJacadastrado() {
		//cenario
		String email = "email@email.com";
		Usuario usuario = Usuario.builder().email(email).build();
		Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);
		//acao
		service.salvarUsuario(usuario);
		//verificar
		Mockito.verify(repository,Mockito.never()).save(usuario);
		
	}
	
	
	
	@Test(expected = Test.None.class)
	public void deveAutenticarUmUsuarioComSucesso() {
		//cenario
		String email = "email@email.com";
		String senha = "senha";
		
		Usuario usuario = Usuario.builder().email(email).senha(senha).id(11).build();
		Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));
		
		// acao
		Usuario result = service.autenticar(email, senha);
		//verificacao
		Assertions.assertThat(result).isNotNull();
	}
	
	@Test// nao encontra email
	public void deveLancarErrorQuandoNaoEncontrarUsauarioCadastradoComOEmailnformado() {
		//cenario
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
		//acao
		Throwable exception = Assertions.catchThrowable( () -> service.autenticar("email@email.com","senha"));
		Assertions.assertThat(exception)
		.isInstanceOf(ErroAutenticacao.class)
		.hasMessage("Usuario nao encontrado para email informado");
	}
	
	@Test// Nao bater
	public void deveLancaErrorQuandoSenhaNaoBater() {
		// cenario
		String senha = "senha";
		Usuario usuario = Usuario.builder().email("email@email.com").senha("senha").build();
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));
		//acao
	    Throwable exception = Assertions.catchThrowable( () ->  service.autenticar("email@email.com","123"));
	    Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Senha invalido");
	}
	
	@Test(expected = Test.None.class)
	public void deveValidarEmail() {
		// cenario
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
		//UsuarioRepository usuariRepository = Mockito.mock(UsuarioRepository.class);
		
		//repository.deleteAll();
		//acao
		service.validarEmail("email@email.com");
		
	}
	
	@Test(expected = RegraNegocioException.class)
	public  void deveLancaErrorAoValidarEmailQuandoExistirEmailCadastrado() {
		
		//cenario
		//Usuario usuario = Usuario.builder().nome("usuario").email("email@email.com").build();
		//repository.save(usuario);
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
		
		// acao
		service.validarEmail("email@email.com");
	}
	
	

}
