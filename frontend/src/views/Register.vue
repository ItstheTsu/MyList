<template>
  <div class="page-full mouse-bg">
    <div class="form-container">
      <div class="welcome-msg">
        <h1>{{ welcomeTitle }}</h1>
        <div class="welcome-rotating">
          <p>{{ currentSubtitle }}</p>
        </div>
      </div>

      <div class="register-container">
        <h1>Cadastro</h1>
        <form @submit.prevent="register">
          <div>
            <input v-model="name" type="text" placeholder="Nome" required />
          </div>
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
          <button type="submit">Cadastrar</button>

          <div class="login-redirect">
            <a @click="goToLogin">Já tem uma conta?</a>
          </div>
        </form>

        <p v-if="errorMessage" class="error">{{ errorMessage }}</p>
        <p v-if="successMessage" class="success">{{ successMessage }}</p>
      </div>
    </div>
    <canvas id="trail"></canvas>
  </div>
</template>

<script>
import api from "../services/api.js";
import "../styles/LoginRegister/LoginRegister.css";
import { welcomeData } from "../data/welcome.js";

export default {
  name: "Register",

  mounted() {
    const canvas = document.getElementById("trail");
    const ctx = canvas.getContext("2d");

    function resize() {
      canvas.width = window.innerWidth;
      canvas.height = window.innerHeight;
    }

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
      ctx.lineWidth = 4;
      ctx.lineCap = "round";

      ctx.beginPath();
      ctx.moveTo(lastX, lastY);
      ctx.lineTo(e.clientX, e.clientY);
      ctx.stroke();

      lastX = e.clientX;
      lastY = e.clientY;
    });

    function fade() {
      ctx.fillStyle = "rgba(255, 255, 255, 0.05)";
      ctx.fillRect(0, 0, canvas.width, canvas.height);
      requestAnimationFrame(fade);
    }
    fade();
    this.typeWriter();
  },

  data() {
    return {
      name: "",
      email: "",
      password: "",
      errorMessage: "",
      successMessage: "",
      welcomeTitle: welcomeData.title,
      subtitles: welcomeData.subtitle,
      currentSubtitle: welcomeData.subtitle[0],
      subtitleIndex: 0,
      typingSpeed: 70,
      deletingSpeed: 40,
      pauseTime: 1500,
    };
  },
  methods: {
    async register() {
      try {
        const response = await api.post("/users", {
          name: this.name,
          email: this.email,
          password: this.password,
        });

        if (response.status === 201 || response.status === 200) {
          const [user, token] = [response.data.user, response.data.token];

          api.defaults.headers.Authorization = `Bearer ${token}`;
          localStorage.setItem("user", JSON.stringify(user));
          localStorage.setItem("token", token);
          this.successMessage =
            "Cadastro realizado! Redirecionando para o Dashboard...";
          this.errorMessage = "";
          setTimeout(() => {
            this.$router.push("/dashboard");
          }, 1000);
        }
      } catch (err) {
        if (err.response && err.response.status === 409) {
          this.errorMessage = "Email já cadastrado. Tente outro.";
        } else {
          this.errorMessage = "Erro inesperado. Tente novamente.";
        }
        this.successMessage = "";
        console.error("Erro no cadastro:", err);
      }
    },

    goToLogin() {
      this.$router.push("/login");
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
