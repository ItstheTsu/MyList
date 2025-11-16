<template>
  <div class="register-container">
    <h1>Cadastro</h1>
    <form @submit.prevent="register">
      <div>
        <label>Nome:</label>
        <input v-model="name" type="text" required />
      </div>
      <div>
        <label>Email:</label>
        <input v-model="email" type="email" required />
      </div>
      <div>
        <label>Senha:</label>
        <input v-model="password" type="password" required />
      </div>
      <button type="submit">Cadastrar</button>
    </form>

    <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
    <p v-if="successMessage" class="success">{{ successMessage }}</p>

    <div class="login-redirect">
      <p>Já tem uma conta?</p>
      <button @click="goToLogin">Entrar</button>
    </div>
  </div>
</template>

<script>
import api from "../services/axios.js";

export default {
  name: "Register",
  data() {
    return {
      name: "",
      email: "",
      password: "",
      errorMessage: "",
      successMessage: "",
    };
  },
  methods: {
    async register() {
      try {
        // Tenta criar o usuário
        const response = await api.post("/users", {
          name: this.name,
          email: this.email,
          password: this.password,
        });

        if (response.status === 201) {
          const user = response.data;
          // Salva no localStorage
          localStorage.setItem("user", JSON.stringify(user));
          this.successMessage =
            "Cadastro realizado! Redirecionando para o Dashboard...";
          this.errorMessage = "";

          // Redireciona pro Dashboard após 1s
          setTimeout(() => {
            this.$router.push("/dashboard");
          }, 1000);
        }
      } catch (err) {
        if (err.response && err.response.status === 409) {
          this.errorMessage = "Email já cadastrado. Tente outro.";
        } else {
          this.errorMessage = "Email já cadastrado. Tente outro.";
        }
        this.successMessage = "";
        console.error("Erro no cadastro:", err);
      }
    },

    goToLogin() {
      this.$router.push("/login");
    },
  },
};
</script>