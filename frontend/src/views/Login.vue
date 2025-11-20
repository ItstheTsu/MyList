<template class="page-full mouse-bg">
  <div class="page-full mouse-bg">
    <div class="form-container">
      <div class="welcome-msg">
        <h1>{{ welcomeTitle }}</h1>
        <div class="welcome-rotating">
          <p>{{ currentSubtitle }}</p>
        </div>
      </div>

      <div class="login-container">
        <h1>Login</h1>
        <form @submit.prevent="login">
          <div>
            <input v-model="email" type="email" placeholder="Email" required />
          </div>

          <div>
            <input
              v-model="password"
              type="password"
              placeholder="Senha"
              required
            />
          </div>

          <button type="submit">Entrar</button>

          <div class="register-redirect">
            <a @click="goToRegister">Não têm uma conta?</a>
          </div>

          <div class="recover-password">
            <a @click="backupPassword">Esqueceu a senha?</a>
          </div>
        </form>

        <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
      </div>
    </div>

    <canvas id="trail"></canvas>
  </div>
</template>

<script>
import api from "../services/api.js";
import { welcomeData } from "../data/welcome.js";
import "../styles/LoginRegister/LoginRegister.css";

export default {
  name: "Login",

  data() {
    return {
      email: "",
      password: "",
      errorMessage: "",

      welcomeTitle: welcomeData.title,
      subtitles: welcomeData.subtitle,
      currentSubtitle: welcomeData.subtitle[0],
      subtitleIndex: 0,
      typingSpeed: 70,
      deletingSpeed: 40,
      pauseTime: 1500,
    };
  },

  mounted() {
    const canvas = document.getElementById("trail");
    const ctx = canvas.getContext("2d");

    const resize = () => {
      canvas.width = window.innerWidth;
      canvas.height = window.innerHeight;
    };

    resize();
    window.addEventListener("resize", resize);

    let lastX = null;
    let lastY = null;

    document.addEventListener("mousemove", (e) => {
      if (lastX === null) {
        lastX = e.clientX;
        lastY = e.clientY;
      }

      ctx.strokeStyle = "rgba(0,0,0,1)";
      ctx.lineWidth = 3;
      ctx.lineCap = "round";

      ctx.beginPath();
      ctx.moveTo(lastX, lastY);
      ctx.lineTo(e.clientX, e.clientY);
      ctx.stroke();

      lastX = e.clientX;
      lastY = e.clientY;
    });

    const fade = () => {
      ctx.fillStyle = "rgba(255,255,255,0.08)";
      ctx.fillRect(0, 0, canvas.width, canvas.height);
      requestAnimationFrame(fade);
    };
    fade();
    this.typeWriter();
  },

  methods: {
    async login() {
      this.errorMessage = "";

      try {
        const response = await api.post("/auth/login", {
          email: this.email,
          password: this.password,
        });

        const user = response.data;
        const token = user.token;

        api.defaults.headers.Authorization = `Bearer ${token}`;
        localStorage.setItem("user", JSON.stringify(user));
        localStorage.setItem("token", token);

        this.$router.push("/dashboard");
      } catch (error) {
        console.error("Erro login:", error);

        if (error.response?.status === 401) {
          if (error.response.data === "Usuário não encontrado") {
            this.errorMessage = "Conta não encontrada, realize o cadastro.";
          } else {
            this.errorMessage = "Email ou senha inválidos!";
          }
        } else {
          this.errorMessage = "Erro inesperado. Tente novamente.";
        }
      }
    },

    goToRegister() {
      this.$router.push("/register");
    },

    backupPassword() {
      this.$router.push("/recover-password");
    },

    typeWriter() {
      const current = this.subtitles[this.subtitleIndex];
      let i = 0;

      const type = () => {
        if (i <= current.length) {
          this.currentSubtitle = current.slice(0, i);
          i++;
          setTimeout(type, this.typingSpeed);
        } else {
          setTimeout(() => this.deleteWriter(), this.pauseTime);
        }
      };

      type();
    },

    deleteWriter() {
      const current = this.subtitles[this.subtitleIndex];
      let i = current.length;

      const del = () => {
        if (i >= 0) {
          this.currentSubtitle = current.slice(0, i);
          i--;
          setTimeout(del, this.deletingSpeed);
        } else {
          this.subtitleIndex = (this.subtitleIndex + 1) % this.subtitles.length;
          this.typeWriter();
        }
      };

      del();
    },
  },
};
</script>
