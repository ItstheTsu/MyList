import { useEffect, useState } from "react";

export function useTypewriter(texts, typingSpeed = 80, eraseSpeed = 50, delay = 1500) {
  const [index, setIndex] = useState(0);
  const [subIndex, setSubIndex] = useState(0); 
  const [deleting, setDeleting] = useState(false);

  useEffect(() => {
    if (!texts || texts.length === 0) return;

    if (subIndex === texts[index].length && !deleting) {
      const timeout = setTimeout(() => setDeleting(true), delay);
      return () => clearTimeout(timeout);
    }

    if (subIndex === 0 && deleting) {
      setDeleting(false);
      setIndex((prev) => (prev + 1) % texts.length);
      return;
    }

    const timeout = setTimeout(() => {
      setSubIndex((prev) => prev + (deleting ? -1 : 1));
    }, deleting ? eraseSpeed : typingSpeed);

    return () => clearTimeout(timeout);
  }, [subIndex, deleting, texts, index, typingSpeed, eraseSpeed, delay]);

  return texts[index].substring(0, subIndex);
}